package control;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Reserva;
import repository.ReservaRepository;
import repository.ServicosRepository;

public class EditarReservaController {

    @FXML private TextField txtCliente;
    @FXML private ComboBox<String> comboServico;
    @FXML private TextField txtData;
    @FXML private TextField txtHorario;
    @FXML private Button btnCancelar;
    @FXML private Button btnSalvar;

    private Reserva reservaSelecionada;

    private final ReservaRepository repo = ReservaRepository.getInstance();
    private final ServicosRepository servicoRepo = ServicosRepository.getInstance();

    @FXML
    public void initialize() {
        comboServico.getItems().addAll(servicoRepo.getAll());
    }

    public void setReserva(Reserva r) {
        this.reservaSelecionada = r;

        txtCliente.setText(r.getCliente());
        comboServico.setValue(r.getServico());
        txtData.setText(r.getData());
        txtHorario.setText(r.getHorario());
    }

    @FXML
    private void onSalvar() {
        Reserva editada = new Reserva(
                txtCliente.getText(),
                comboServico.getValue(),
                txtData.getText(),
                txtHorario.getText()
        );
        repo.update(reservaSelecionada.getId(), editada);
        fechar();
    }

    @FXML
    private void onCancelar() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) txtCliente.getScene().getWindow();
        stage.close();
    }
}