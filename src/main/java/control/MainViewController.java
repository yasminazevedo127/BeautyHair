package control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ReservaFX;
import repository.ReservaRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainViewController {

    @FXML private BorderPane root;
    @FXML private TableView<ReservaFX> tabela;
    @FXML private TableColumn<ReservaFX, String> colCliente;
    @FXML private TableColumn<ReservaFX, String> colServico;
    @FXML private TableColumn<ReservaFX, String> colData;
    @FXML private TableColumn<ReservaFX, String> colHorario;
    @FXML private TableColumn<ReservaFX, Void> colAcoes;

    @FXML private TextField txtPesquisar;
    @FXML private Button btnPesquisar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnNovaReserva;
    @FXML private Button btnGerenciarServicos;

    private final ReservaRepository repo = ReservaRepository.getInstance();

    @FXML
    public void initialize() {
        colCliente.setCellValueFactory(cell -> cell.getValue().clienteProperty());
        colServico.setCellValueFactory(cell -> cell.getValue().servicoProperty());
        colData.setCellValueFactory(cell -> cell.getValue().dataProperty());
        colHorario.setCellValueFactory(cell -> cell.getValue().horarioProperty());

        configurarAcoes();
        carregarTabela();
    }

    @FXML
    public void abrirCadastrar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CadastrarReserva.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Cadastrar Reserva");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tabela.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            carregarTabela();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void atualizarTabela() {
        carregarTabela();
    }

    @FXML
    public void pesquisar() {
        String termo = txtPesquisar.getText();
        List<ReservaFX> fxList = repo.search(termo).stream()
            .map(ReservaFX::fromReserva)
            .toList();
        tabela.getItems().setAll(fxList);
    }



    private void carregarTabela() {
        List<ReservaFX> fxList = repo.getAll().stream()
            .map(ReservaFX::fromReserva)
            .toList();
        tabela.getItems().setAll(fxList);
    }


    private void configurarAcoes() {
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("✎");
            private final Button btnExcluir = new Button("✖");
            private final HBox box = new HBox(6, btnEditar, btnExcluir);

            {
                btnEditar.getStyleClass().add("action-button");
                btnExcluir.getStyleClass().add("action-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                ReservaFX rfx = getTableView().getItems().get(getIndex());

                btnEditar.setOnAction(e -> abrirEditar(rfx));
                btnExcluir.setOnAction(e -> {
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                            "Remover reserva de " + rfx.getCliente() + "?",
                            ButtonType.YES, ButtonType.NO);
                    a.initOwner(tabela.getScene().getWindow());
                    Optional<ButtonType> resp = a.showAndWait();
                    if (resp.isPresent() && resp.get() == ButtonType.YES) {
                        repo.removeById(rfx.getId());
                        carregarTabela();
                    }
                });

                setGraphic(box);
            }
        });
    }


	private void abrirEditar(ReservaFX rfx) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditarReserva.fxml"));
	        Parent root = loader.load();
	        EditarReservaController ctrl = loader.getController();
	
	        ctrl.setReserva(rfx.toReserva());
	
	        Stage stage = new Stage();
	        stage.setTitle("Editar Reserva");
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.initOwner(tabela.getScene().getWindow());
	        stage.setScene(new Scene(root));
	        stage.showAndWait();
	
	        carregarTabela();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	@FXML
	private void abrirGerenciarServicos() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Servico.fxml"));
	        Parent root = loader.load();

	        Stage stage = new Stage();
	        stage.setTitle("Gerenciar Serviços");
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.initOwner(tabela.getScene().getWindow());
	        stage.setScene(new Scene(root));
	        stage.setMinWidth(350);
	        stage.setMinHeight(400);
	        stage.showAndWait();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}