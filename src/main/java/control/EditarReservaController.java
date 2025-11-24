package control;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Reserva;
import repository.ReservaRepository;

public class EditarReservaController {

    @FXML private TextField txtCliente;
    @FXML private TextField txtServico;
    @FXML private TextField txtProfissional;
    @FXML private TextField txtData;
    @FXML private TextField txtHorario;

    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    private int index;
    private ReservaRepository repository;

    public void setDados(Reserva reserva, int index, ReservaRepository repo) {
        this.index = index;
        this.repository = repo;

        txtCliente.setText(reserva.getCliente());
        txtServico.setText(reserva.getServico());
        txtData.setText(reserva.getData());
        txtHorario.setText(reserva.getHorario());
    }

    @FXML
    private void initialize() {
        btnCancelar.setOnAction(e -> fechar());

        btnSalvar.setOnAction(e -> {
            Reserva nova = new Reserva(
                txtCliente.getText(),
                txtServico.getText(),
                txtData.getText(),
                txtHorario.getText()
            );

            repository.update(index, nova);
            fechar();
        });
    }

    private void fechar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}