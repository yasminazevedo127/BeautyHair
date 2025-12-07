package control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import repository.ProfissionaisRepository;
import repository.ReservaRepository;
import repository.ServicosRepository;
import services.ReservaValidator;
import model.Reserva;

import java.time.LocalTime;
import java.util.List;

public class CadastrarReservaController extends ControleBase {

    @FXML private TextField txtCliente;
    @FXML private ComboBox<String> comboServico;
    @FXML private ComboBox<String> comboProfissional;
    @FXML private DatePicker dateData;
    @FXML private ComboBox<LocalTime> comboHorario;
    @FXML private ComboBox<String> comboStatus;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    private final ReservaRepository repo = ReservaRepository.getInstance();
    private final ServicosRepository servRepo = ServicosRepository.getInstance();
    private final ProfissionaisRepository profRepo = ProfissionaisRepository.getInstance();

    private final ReservaValidator validator = new ReservaValidator();
    
    @FXML
    public void initialize() {
        comboServico.getItems().setAll(servRepo.getAll());
        comboProfissional.getItems().setAll(profRepo.getAll());
        
        comboStatus.getItems().addAll(
        	    "Agendada",
        	    "Concluída",
        	    "Cancelada",
        	    "Não Compareceu"
        	);
        comboStatus.getSelectionModel().select("Agendada"); 
        
        JavaFXUtils.carregarHorarios(comboHorario);
        JavaFXUtils.configurarDatePicker(dateData);

        btnSalvar.setOnAction(e -> onSalvar());
        btnCancelar.setOnAction(this::onCancelar);
    }

    @FXML
    private void onSalvar() {
        Reserva r = new Reserva();
        r.setCliente(txtCliente.getText().trim());
        r.setServico(comboServico.getValue());
        r.setProfissional(comboProfissional.getValue());
        r.setStatus(comboStatus.getValue());
        
        r.setData(dateData.getValue() != null ? dateData.getValue().format(JavaFXUtils.DATE_FMT) : null);
        r.setHorario(comboHorario.getValue() != null ? comboHorario.getValue().format(JavaFXUtils.TIME_FMT) : null);
        
        List<String> erros = validator.validar(r);

        if (erros.isEmpty()) { 
            if (validator.existeConflito(r)) { 
                erros.add("Já existe reserva para este profissional neste horário!");
            }
        }
        
        if (!erros.isEmpty()) {
            String mensagemErro = "Não foi possível cadastrar a reserva devido aos seguintes erros:\n\n* " 
                                + String.join("\n* ", erros);
            
            erro(mensagemErro); 
            return; 
        }

        repo.add(r);
        sucesso("Reserva cadastrada com sucesso!"); 
        fechar(btnSalvar);
    }
    
    @FXML
	protected void onCancelar(ActionEvent event) {
        super.onCancelar(event); 
    }
}