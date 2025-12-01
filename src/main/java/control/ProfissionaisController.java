package control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import repository.ProfissionaisRepository;

import java.util.Optional;

public class ProfissionaisController {

    @FXML private ListView<String> listaProfissionais;
    @FXML private Button btnAdicionar;

    private final ProfissionaisRepository repo = ProfissionaisRepository.getInstance();

    @FXML
    private void initialize() {
        carregarLista();

        btnAdicionar.setOnAction(e -> abrirAdicionarProfissional());

        listaProfissionais.setCellFactory(lv -> new ListCell<>() {
            private final Button btnRemover = new Button("✖");
            private final HBox hbox = new HBox(10);

            {
                hbox.getChildren().add(btnRemover);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setGraphic(hbox);

                    btnRemover.setOnAction(e -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                                "Remover profissional '" + item + "'?",
                                ButtonType.YES, ButtonType.NO);
                        alert.initOwner(getScene().getWindow());
                        Optional<ButtonType> resp = alert.showAndWait();

                        if (resp.isPresent() && resp.get() == ButtonType.YES) {
                            repo.remove(item);
                            carregarLista();
                        }
                    });
                }
            }
        });
    }

    private void carregarLista() {
        listaProfissionais.getItems().setAll(repo.getAll());
    }

    private void abrirAdicionarProfissional() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdicionarItem.fxml"));
            Parent root = loader.load();

            AdicionarItemController controller = loader.getController();
            controller.configurar(
                    "Adicionar Profissional",
                    "Nome do profissional",
                    nome -> {
                        repo.add(nome);
                        carregarLista();
                    }
            );

            Stage stage = new Stage();
            stage.setTitle("Novo Profissional");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}