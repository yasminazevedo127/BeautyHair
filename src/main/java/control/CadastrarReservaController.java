package control;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Reserva;
import repository.ReservaRepository;
import repository.ServicosRepository;

public class CadastrarReservaController {

    @FXML private TextField txtCliente;
    @FXML private ComboBox<String> comboServico;
    @FXML private TextField txtData;
    @FXML private TextField txtHorario;
    @FXML private Button btnCancelar;
    @FXML private Button btnSalvar;

    private final ReservaRepository repo = ReservaRepository.getInstance();
    private final ServicosRepository servicoRepo = ServicosRepository.getInstance();

    @FXML
    public void initialize() {
        comboServico.getItems().addAll(servicoRepo.getAll());
    }

    @FXML
    private void onSalvar() {
        Reserva r = new Reserva(
            txtCliente.getText(),
            comboServico.getValue(),
            txtData.getText(),
            txtHorario.getText()
        );
        repo.add(r);  
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