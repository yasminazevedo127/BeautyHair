package control;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class AdicionarItemController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNome;

    private Consumer<String> onSave;

    public void configurar(String titulo, String placeholder, Consumer<String> onSave) {
        lblTitulo.setText(titulo);
        txtNome.setPromptText(placeholder);
        this.onSave = onSave;
    }

    @FXML
    private void salvar() {
        String nome = txtNome.getText();
        if (nome == null || nome.isBlank()) return;

        if (onSave != null) onSave.accept(nome);

        fechar();
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