package control;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Reserva;
import repository.ReservaRepository;

public class MainViewController {

    @FXML private TableView<Reserva> tableReservas;

    @FXML private TableColumn<Reserva, String> colNome;
    @FXML private TableColumn<Reserva, String> colServico;
    @FXML private TableColumn<Reserva, String> colData;
    @FXML private TableColumn<Reserva, String> colHorario;
    @FXML private TableColumn<Reserva, Void> colAcoes;

    @FXML private Button btnNovaReserva;
    @FXML private TextField txtPesquisar;
    @FXML private Button btnPesquisar;
    @FXML private Button btnAtualizar;

    private final ReservaRepository repository = new ReservaRepository();
    private ObservableList<Reserva> listaOriginal;

    @FXML
    public void initialize() {

        colNome.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCliente()));
        colServico.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getServico()));
        colData.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getData()));
        colHorario.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getHorario()));

        carregarTabela();
        configurarColunaAcoes();

        btnNovaReserva.setOnAction(e -> novaReserva());
        btnPesquisar.setOnAction(e -> pesquisar());
        txtPesquisar.textProperty().addListener((obs, o, n) -> pesquisar());
        btnAtualizar.setOnAction(e -> carregarTabela());
    }

    
    private void carregarTabela() {
        listaOriginal = FXCollections.observableArrayList(repository.getAll());
        tableReservas.setItems(listaOriginal);
    }

    
    private void configurarColunaAcoes() {

        colAcoes.setCellFactory(col -> new TableCell<>() {

            private final HBox container = new HBox(8);

            private final Button btnEditar = criarBotaoSVG(
                    "M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z "
                  + "M20.71 7.04a1.003 1.003 0 0 0 0-1.42l-2.34-2.34a1.003 1.003 0 0 0-1.42 0"
                  + "l-1.83 1.83 3.75 3.75 1.84-1.82z",
                    "#ff69b4"
            );

            private final Button btnExcluir = criarBotaoSVG(
                    "M6 6l12 12M18 6L6 18",
                    "red"
            );

            {
                container.getChildren().addAll(btnEditar, btnExcluir);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Reserva reserva = tableReservas.getItems().get(getIndex());

                btnEditar.setOnAction(e -> editarReserva(reserva));
                btnExcluir.setOnAction(e -> excluirReserva(reserva));

                setGraphic(container);
            }
        });
    }

    private Button criarBotaoSVG(String path, String color) {
        SVGPath svg = new SVGPath();
        svg.setContent(path);
        svg.setStyle("-fx-fill: " + color + "; -fx-scale-x: 0.7; -fx-scale-y: 0.7;");

        Button b = new Button();
        b.setStyle("-fx-background-color: transparent;");
        b.setGraphic(svg);
        return b;
    }

    
    private void novaReserva() {}

    private void pesquisar() {
        String termo = txtPesquisar.getText().toLowerCase();

        if (termo.isBlank()) {
            tableReservas.setItems(listaOriginal);
            return;
        }

        ObservableList<Reserva> filtrada = FXCollections.observableArrayList();

        for (Reserva r : listaOriginal) {
            if (containsIgnoreCase(r.getCliente(), termo) ||
                containsIgnoreCase(r.getServico(), termo) ||
                containsIgnoreCase(r.getData(), termo) ||
                containsIgnoreCase(r.getHorario(), termo)) {

                filtrada.add(r);
            }
        }

        tableReservas.setItems(filtrada);
    }

    private boolean containsIgnoreCase(String value, String termo) {
        return value != null && value.toLowerCase().contains(termo);
    }

    private void editarReserva(Reserva r) {}

    private void excluirReserva(Reserva r) {
        repository.removerPorId(r.getId());
        carregarTabela();
    }
}