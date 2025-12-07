package control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Reserva;
import repository.ProfissionaisRepository;
import repository.ReservaRepository;
import repository.ServicosRepository;
import services.ReservaValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EditarReservaController extends ControleBase {

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
    
    private Reserva reservaAtual;
    
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
        
        JavaFXUtils.carregarHorarios(comboHorario);
        JavaFXUtils.configurarDatePicker(dateData);
        
        btnSalvar.setOnAction(e -> onSalvar());
        btnCancelar.setOnAction(this::onCancelar);
    }
    

    public void setReserva(Reserva r) {
        this.reservaAtual = r;

        txtCliente.setText(r.getCliente());
        comboServico.setValue(r.getServico());
        comboProfissional.setValue(r.getProfissional());
        comboStatus.setValue(r.getStatus());
        dateData.setValue(r.getDataAsLocalDate());
        comboHorario.setValue(r.getHorarioAsLocalTime());

        bloquearCamposSePassado();
    } 
    

    @FXML
    private void onSalvar() {
        LocalDate data = dateData.getValue();
        LocalTime horario = comboHorario.getValue();
        
        if (isReservaPassada(data, horario)) { 
            atualizarStatus();
        } else {
            atualizarReservaCompleta(data, horario);
        }
    }
    
    
    private void atualizarStatus() {
        String status = comboStatus.getValue();
        this.reservaAtual.setStatus(status);
        repo.update(reservaAtual.getId(), reservaAtual);
        sucesso("Status atualizado com sucesso!");
        fechar(btnSalvar);
    }
    
    
    private void atualizarReservaCompleta(LocalDate data, LocalTime horario) {
        
        reservaAtual.setCliente(txtCliente.getText().trim());
        reservaAtual.setServico(comboServico.getValue());
        reservaAtual.setProfissional(comboProfissional.getValue());
        reservaAtual.setStatus(comboStatus.getValue());
        
        reservaAtual.setData(data != null ? data.format(JavaFXUtils.DATE_FMT) : null);
        reservaAtual.setHorario(horario != null ? horario.format(JavaFXUtils.TIME_FMT) : null);

        List<String> erros = validator.validar(reservaAtual); 

        if (erros.isEmpty()) { 
            if (validator.existeConflito(reservaAtual, reservaAtual.getId())) { 
                erros.add("Já existe reserva para este profissional neste horário!");
            }
        }
        
        if (!erros.isEmpty()) {
            String mensagemErro = "Não foi possível atualizar a reserva devido aos seguintes erros:\n\n* " 
                                + String.join("\n* ", erros);
            erro(mensagemErro);
            return; 
        }

        repo.update(reservaAtual.getId(), reservaAtual);
        sucesso("Reserva atualizada com sucesso!");
        fechar(btnSalvar);
    }
    
    
    @FXML
	protected void onCancelar(ActionEvent event) {
        super.onCancelar(event); 
    }
    
    
    private void bloquearCamposSePassado() {

    	LocalDate dataReserva = reservaAtual.getDataAsLocalDate();
        LocalTime horaReserva = reservaAtual.getHorarioAsLocalTime();
        
        boolean passado = false;
        if (dataReserva != null && horaReserva != null) {
            LocalDate hoje = LocalDate.now();
            LocalTime agora = LocalTime.now();
            
            passado = dataReserva.isBefore(hoje) ||
                      (dataReserva.isEqual(hoje) && horaReserva.isBefore(agora));
        }

        if (passado) {

            txtCliente.setDisable(true);
            comboServico.setDisable(true);
            comboProfissional.setDisable(true);
            dateData.setDisable(true);
            comboHorario.setDisable(true);

            comboStatus.setDisable(false);

            btnSalvar.setDisable(false);
            btnCancelar.setDisable(false);
        }
    }
    
    
    private boolean isReservaPassada(LocalDate data, LocalTime horario) {
        if (data == null || horario == null) {
            return false;
        }
        
        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        return data.isBefore(hoje) || 
               (data.isEqual(hoje) && horario.isBefore(agora));
    }
}