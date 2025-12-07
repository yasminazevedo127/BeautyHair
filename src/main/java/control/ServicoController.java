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

import java.util.Optional;

public class ServicoController extends ControleBase { 

    @FXML private ListView<String> listaServicos;
    @FXML private Button btnAdicionar;

    private final ServicosRepository repo = ServicosRepository.getInstance();

    @FXML
    private void initialize() {
        carregarLista();

        btnAdicionar.setOnAction(e -> abrirAdicionarServico());

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
                        Optional<ButtonType> resp = ServicoController.this.confirmacao("Remover serviço '" + item + "'?").showAndWait();
                        
                        if (resp.isPresent() && resp.get() == ButtonType.YES) {
                            repo.remove(item);
                            carregarLista();
                            ServicoController.this.sucesso("Serviço removido com sucesso!"); 
                        }
                    });
                }
            }
        });
    }
    

    private void carregarLista() {
        listaServicos.getItems().setAll(repo.getAll());
    }

    private void abrirAdicionarServico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdicionarItem.fxml"));
            Parent root = loader.load();

            AdicionarItemController controller = loader.getController();
            
            controller.configurar(
                    "Adicionar Serviço",
                    "Nome do serviço",
                    nome -> {
                        repo.add(nome);
                        carregarLista(); 
                        sucesso("Serviço '" + nome + "' adicionado com sucesso!"); 
                    }
            );

            Stage stage = new Stage();
            stage.setTitle("Novo Serviço");
            stage.initModality(Modality.APPLICATION_MODAL);
            // Define o dono do Stage
            stage.initOwner(listaServicos.getScene().getWindow()); 
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            erro("Não foi possível carregar o formulário de adição de item. Verifique o console.");
        }
    }
}