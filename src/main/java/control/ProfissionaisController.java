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

public class ProfissionaisController extends ControleBase { 

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
                        Optional<ButtonType> resp = ProfissionaisController.this.confirmacao("Remover profissional '" + item + "'?").showAndWait();

                        if (resp.isPresent() && resp.get() == ButtonType.YES) {
                            repo.remove(item);
                            carregarLista();
                            ProfissionaisController.this.sucesso("Profissional removido com sucesso!");
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
                        sucesso("Profissional '" + nome + "' adicionado com sucesso!");
                    }
            );

            Stage stage = new Stage();
            stage.setTitle("Novo Profissional");
            stage.initModality(Modality.APPLICATION_MODAL);
            // 💡 Define o dono do Stage (melhor UX)
            stage.initOwner(listaProfissionais.getScene().getWindow()); 
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            erro("Não foi possível carregar o formulário de adição de profissional. Verifique o console.");
        }
    }
}