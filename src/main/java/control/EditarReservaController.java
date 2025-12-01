package control;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Reserva;
import repository.ReservaRepository;
import repository.ServicosRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditarReservaController {

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
    private final ServicosRepository profRepo = ServicosRepository.getInstance();
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Reserva reservaAtual;
    
    Locale localeBR = Locale.of("pt", "BR");
    
    @FXML
    public void initialize() {
        comboServico.getItems().setAll(servRepo.getAll());
        comboProfissional.getItems().setAll(profRepo.getAll());
        comboStatus.getItems().setAll("Agendada", "Concluída", "Cancelada");
        
        carregarHorarios();
        configurarDatePicker();
        
        btnSalvar.setOnAction(e -> onSalvar());
        btnCancelar.setOnAction(e -> fechar());
    }

    public void setReserva(Reserva r) {
        if (r == null) return;
        
        this.reservaAtual = r;

        txtCliente.setText(r.getCliente());
        comboServico.setValue(r.getServico());
        comboProfissional.setValue(r.getProfissional());
        comboStatus.setValue(r.getStatus());

        if (r.getDataAsLocalDate() != null) {
            dateData.setValue(r.getDataAsLocalDate());
        }

        if (r.getHorarioAsLocalTime() != null) {
            comboHorario.setValue(r.getHorarioAsLocalTime());
        }
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
                setText(empty ? null : item.format(TIME_FMT));
            }
        });
        comboHorario.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(TIME_FMT));
            }
        });
    }

    @FXML
    private void onSalvar() {
        String cliente = txtCliente.getText().trim();
        String servico = comboServico.getValue();
        String profissional = comboProfissional.getValue();
        String status = comboStatus.getValue();
        LocalDate data = dateData.getValue();
        LocalTime horario = comboHorario.getValue();

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

        reservaAtual.setCliente(cliente);
        reservaAtual.setServico(servico);
        reservaAtual.setProfissional(profissional);
        reservaAtual.setStatus(status);
        reservaAtual.setData(data.format(DATE_FMT));
        reservaAtual.setHorario(horario.format(TIME_FMT));
        
        if (repo.getAll().stream()
                .anyMatch(res -> res.conflitaCom(reservaAtual))) {
            aviso("Já existe reserva para este profissional neste horário!");
            return;
        }

        repo.update(reservaAtual.getId(), reservaAtual); 
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