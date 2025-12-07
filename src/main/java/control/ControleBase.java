package control; 

import javafx.event.ActionEvent;
import javafx.scene.Node; 
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public abstract class ControleBase {

	protected void aviso(String texto) {
        criarAlerta(texto, Alert.AlertType.WARNING).showAndWait();
    }
    
    protected void sucesso(String texto) {
        criarAlerta(texto, Alert.AlertType.INFORMATION).showAndWait();
    }
    
    protected void erro(String texto) {
        criarAlerta(texto, Alert.AlertType.ERROR).showAndWait();
    }
    
    private Alert criarAlerta(String texto, Alert.AlertType tipo) {
        Alert a = new Alert(tipo, texto, ButtonType.OK);
        a.setHeaderText(null);
        return a;
    }

    protected void fechar(Node referenceNode) {
        Stage stage = (Stage) referenceNode.getScene().getWindow();
        stage.close();
    }

    protected void onCancelar(ActionEvent event) {
        Node source = (Node) event.getSource();
        fechar(source);
    }
    
    protected Alert confirmacao(String texto) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, texto, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        return a;
    }
}
