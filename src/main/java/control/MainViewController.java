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
import javafx.beans.property.SimpleStringProperty;
import model.ReservaFX;
import repository.ReservaRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class MainViewController {

    @FXML private BorderPane root;
    @FXML private TableView<ReservaFX> tabela;
    @FXML private TableColumn<ReservaFX, String> colCliente;
    @FXML private TableColumn<ReservaFX, String> colServico;
    @FXML private TableColumn<ReservaFX, String> colProfissional;
    @FXML private TableColumn<ReservaFX, String> colData;
    @FXML private TableColumn<ReservaFX, String> colHorario;
    @FXML private TableColumn<ReservaFX, String> colStatus;
    @FXML private TableColumn<ReservaFX, Void> colAcoes;

    @FXML private TextField txtPesquisar;
    @FXML private Button btnPesquisar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnNovaReserva;
    @FXML private Button btnGerenciarServicos;
    @FXML private Button btnGerenciarProfissionais;
    
    @FXML private Label lblHoje;
    @FXML private Button btnMostrarTudo;

    private final ReservaRepository repo = ReservaRepository.getInstance();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
    	btnNovaReserva.setMaxHeight(Double.MAX_VALUE);
        btnGerenciarServicos.setMaxHeight(Double.MAX_VALUE);
        btnGerenciarProfissionais.setMaxHeight(Double.MAX_VALUE);
        
    	lblHoje.setText("Hoje • " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    	
        colCliente.setCellValueFactory(cell -> cell.getValue().clienteProperty());
        colServico.setCellValueFactory(cell -> cell.getValue().servicoProperty());
        colProfissional.setCellValueFactory(cell -> cell.getValue().profissionalProperty());
        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());
        colData.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getData() != null
                ? cell.getValue().getData().format(DATE_FMT) : ""));
        colHorario.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().getHorario() != null
                ? cell.getValue().getHorario().format(TIME_FMT) : ""));

        configurarAcoes();
        carregarTabela();

        btnMostrarTudo.setOnAction(e -> carregarTabelaCompleta());
    }

    @FXML
    public void abrirCadastrar() {
        abrirFormulario("/view/CadastrarReserva.fxml", null);
    }

    private void abrirFormulario(String fxmlPath, ReservaFX reservaFX) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Controller do form
            Object controller = loader.getController();
            if (reservaFX != null && controller instanceof EditarReservaController editarCtrl) {
                editarCtrl.setReserva(reservaFX.toReserva());
            }

            Stage stage = new Stage();
            stage.setTitle(reservaFX == null ? "Cadastrar Reserva" : "Editar Reserva");
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
            .sorted((a, b) -> {
                int compData = a.getData().compareTo(b.getData());
                return compData != 0 ? compData : a.getHorario().compareTo(b.getHorario());
            })
            .toList();

        tabela.getItems().setAll(fxList);
    }
    
    @FXML
    private void mostrarTudo() {
        carregarTabelaCompleta();
    }

    private void carregarTabela() {
        var hoje = LocalDate.now();

        List<ReservaFX> fxList = repo.getAll().stream()
            .filter(r -> {
                LocalDate d = r.getDataAsLocalDate();
                return d != null && d.equals(hoje);
            })
            .sorted((a, b) -> a.getHorarioAsLocalTime().compareTo(b.getHorarioAsLocalTime()))
            .map(ReservaFX::fromReserva)
            .toList();

        tabela.getItems().setAll(fxList);
    }
    
    private void carregarTabelaCompleta() {
        List<ReservaFX> fxList = repo.getAll().stream()
            .sorted((a, b) -> {
                LocalDate da = a.getDataAsLocalDate();
                LocalDate db = b.getDataAsLocalDate();

                int compData = da.compareTo(db);
                if (compData != 0) return compData;

                return a.getHorarioAsLocalTime().compareTo(b.getHorarioAsLocalTime());
            })
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

                btnEditar.setOnAction(e -> abrirFormulario("/view/EditarReserva.fxml", rfx));
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
    
    @FXML
    private void abrirGerenciarProfissionais() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Profissionais.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gerenciar Profissionais");
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