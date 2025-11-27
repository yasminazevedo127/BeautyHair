package control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import repository.ServicosRepository;

import java.io.IOException;
import java.util.Optional;

public class ServicoController {

    @FXML private ListView<String> listaServicos;
    @FXML private Button btnAdicionarServico;

    private final ServicosRepository repo = ServicosRepository.getInstance();

    @FXML
    private void initialize() {
        carregarLista();

        btnAdicionarServico.setOnAction(e -> adicionarServico());

        listaServicos.setCellFactory(lv -> new ListCell<>() {
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
                                "Remover serviço '" + item + "'?",
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
        listaServicos.getItems().setAll(repo.getAll());
    }

    private void adicionarServico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdicionarServico.fxml"));
            Parent root = loader.load();

            AdicionarServicoController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Adicionar Serviço");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.salvou()) {
                carregarLista();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}