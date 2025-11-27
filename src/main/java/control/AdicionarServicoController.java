package control;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import repository.ServicosRepository;

public class AdicionarServicoController {

    @FXML private TextField txtNome;

    private boolean salvou = false;
    public boolean salvou() { return salvou; }

    @FXML
    private void salvar() {
        String nome = txtNome.getText().trim();

        if (!nome.isBlank()) {
            ServicosRepository.getInstance().add(nome);
            salvou = true;
            fechar();
        }
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }
}