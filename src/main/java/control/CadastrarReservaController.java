package control;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import repository.ProfissionaisRepository;
import repository.ReservaRepository;
import repository.ServicosRepository;
import model.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CadastrarReservaController {

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

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @FXML
    public void initialize() {
        comboServico.getItems().setAll(servRepo.getAll());
        comboProfissional.getItems().setAll(profRepo.getAll());
        comboStatus.getItems().addAll(
        	    "Agendado",
        	    "Concluído",
        	    "Cancelado",
        	    "Não Compareceu"
        	);
        comboStatus.getSelectionModel().select("Agendado"); 
        
        carregarHorarios();
        configurarDatePicker();

        btnSalvar.setOnAction(e -> onSalvar());
        btnCancelar.setOnAction(e -> fechar());
    }

    private void carregarHorarios() {
        List<LocalTime> horarios = new ArrayList<>();
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fim = LocalTime.of(18, 0);
        while (!inicio.isAfter(fim)) {
            horarios.add(inicio);
            inicio = inicio.plusMinutes(30);
        }
        comboHorario.getItems().setAll(horarios);

        comboHorario.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(TIME_FMT));
            }
        });
        comboHorario.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(TIME_FMT));
            }
        });
    }
    
    private void configurarDatePicker() {
        dateData.setPromptText("dd/MM/yyyy");
        dateData.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? DATE_FMT.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isBlank()) return null;
                return LocalDate.parse(string, DATE_FMT);
            }
        });
    }

    @FXML
    private void onSalvar() {
        String cliente = txtCliente.getText().trim();
        String servico = comboServico.getValue();
        String profissional = comboProfissional.getValue();
        LocalDate data = dateData.getValue();
        LocalTime horario = comboHorario.getValue();
        String status = comboStatus.getValue();

        if (cliente.isEmpty()) {
            aviso("Campo 'Cliente' é obrigatório!");
            return;
        }

        if (servico.isEmpty()) {
            aviso("Campo 'Serviço' é obrigatório!");
            return;
        }
        
        if (profissional == null || profissional.isBlank()) {
            aviso("Escolha um profissional.");
            return;
        }

        if (data == null) {
            aviso("Escolha uma data válida!");
            return;
        }

        if (!validarHorario(horario)) {
            aviso("Escolha um horário válido!");
            return;
        }

        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        if (data.isBefore(hoje) || (data.isEqual(hoje) && horario.isBefore(agora))) {
            aviso("A data e horário escolhidos já passaram!");
            return;
        }
        
        Reserva r = new Reserva();
        r.setCliente(cliente);
        r.setServico(servico);
        r.setProfissional(profissional);
        r.setData(data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        r.setHorario(horario.format(DateTimeFormatter.ofPattern("HH:mm")));
        r.setStatus(status);
        
        if (repo.getAll().stream()
                .anyMatch(res -> res.conflitaCom(r))) {
            aviso("Já existe reserva para este profissional neste horário!");
            return;
        }

        repo.add(r); 
        fechar();
    }
    
    
    @FXML
    private void onCancelar() {
        fechar();
    }

    private boolean validarHorario(LocalTime horario) {
        return horario != null;
    }

    private void aviso(String texto) {
        Alert a = new Alert(Alert.AlertType.WARNING, texto, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void fechar() {
        Stage stage = (Stage) txtCliente.getScene().getWindow();
        stage.close();
    }
}