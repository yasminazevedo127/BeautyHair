package control;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class AdicionarItemController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNome;
    
    private static final String STYLE_ERROR = "-fx-border-color: #f44336; -fx-border-width: 2;";
    private static final String STYLE_DEFAULT = "-fx-border-color: #ccc;";

    private Consumer<String> onSalvar;

    public void configurar(String titulo, String placeholder, Consumer<String> onSalvar) {
        lblTitulo.setText(titulo);
        txtNome.setPromptText(placeholder);
        this.onSalvar = onSalvar;
        
        txtNome.setStyle(STYLE_DEFAULT);
    }

    @FXML
    private void salvar() {
        String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
       
        if (nome.isBlank()) { 
            aplicarEstiloErro(true);
            return;
        }
        
        aplicarEstiloErro(false); 

        if (onSalvar != null) onSalvar.accept(nome);

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
    
    private void aplicarEstiloErro(boolean erro) {
        if (erro) {
            txtNome.setStyle(STYLE_ERROR);
        } else {
            txtNome.setStyle(STYLE_DEFAULT);
        }
    }
}