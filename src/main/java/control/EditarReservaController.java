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
        this.reservaAtual = r;

        txtCliente.setText(r.getCliente());
        comboServico.setValue(r.getServico());
        comboProfissional.setValue(r.getProfissional());
        comboStatus.setValue(r.getStatus());
        dateData.setValue(r.getDataAsLocalDate());
        comboHorario.setValue(r.getHorarioAsLocalTime());

        bloquearCamposSePassado();
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

        String status = comboStatus.getValue();
        LocalDate data = dateData.getValue();
        LocalTime horario = comboHorario.getValue();

        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        boolean reservaPassada =
                data.isBefore(hoje) ||
                (data.isEqual(hoje) && horario.isBefore(agora));

        if (reservaPassada) {
            repo.updateStatus(reservaAtual.getId(), status);
            aviso("Status atualizado com sucesso!");
            fechar();
            return;
        }

        if (horario == null) {
            aviso("Escolha um horário válido!");
            return;
        }

        String cliente = txtCliente.getText().trim();
        String servico = comboServico.getValue();
        String profissional = comboProfissional.getValue();

        if (cliente.isEmpty()) {
            aviso("Campo 'Cliente' é obrigatório!");
            return;
        }

        if (servico == null || servico.isBlank()) {
            aviso("Campo 'Serviço' é obrigatório!");
            return;
        }

        if (profissional == null || profissional.isBlank()) {
            aviso("Escolha um profissional.");
            return;
        }

        reservaAtual.setCliente(cliente);
        reservaAtual.setServico(servico);
        reservaAtual.setProfissional(profissional);
        reservaAtual.setStatus(status);
        reservaAtual.setData(data.format(DATE_FMT));
        reservaAtual.setHorario(horario.format(TIME_FMT));

        if (repo.getAll().stream()
                .anyMatch(r -> r.conflitaCom(reservaAtual))) {
            aviso("Já existe reserva para este profissional neste horário!");
            return;
        }

        repo.update(reservaAtual.getId(), reservaAtual);
        aviso("Reserva atualizada com sucesso!");
        fechar();
    }
    
    @FXML
    private void onCancelar() {
        fechar();
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
    
    private void bloquearCamposSePassado() {

        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        boolean passado =
                reservaAtual.getDataAsLocalDate().isBefore(hoje) ||
                (reservaAtual.getDataAsLocalDate().isEqual(hoje) &&
                 reservaAtual.getHorarioAsLocalTime().isBefore(agora));

        if (passado) {

            txtCliente.setDisable(true);
            comboServico.setDisable(true);
            comboProfissional.setDisable(true);
            dateData.setDisable(true);
            comboHorario.setDisable(true);

            comboStatus.setDisable(false);

            btnSalvar.setDisable(false);
            btnCancelar.setDisable(false);

            String cinza = "-fx-opacity: 0.6;";
            txtCliente.setStyle(cinza);
            comboServico.setStyle(cinza);
            comboProfissional.setStyle(cinza);
            dateData.setStyle(cinza);
            comboHorario.setStyle(cinza);
        }
    }
}