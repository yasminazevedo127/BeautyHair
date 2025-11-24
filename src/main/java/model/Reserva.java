package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Reserva {

    private static int contadorId = 1;

    private int id;

    private final StringProperty cliente = new SimpleStringProperty();
    private final StringProperty servico = new SimpleStringProperty();
    private final StringProperty data = new SimpleStringProperty();
    private final StringProperty horario = new SimpleStringProperty();

    public Reserva() {
        this.id = contadorId++;
    }

    public Reserva(String cliente, String servico, String data, String horario) {
        this.id = contadorId++;
        this.cliente.set(cliente);
        this.servico.set(servico);
        this.data.set(data);
        this.horario.set(horario);
    }

    public int getId() {
        return id;
    }

    public static void setProximoId(int novoId) {
        contadorId = novoId;
    }

    public String getCliente() {
        return cliente.get();
    }

    public void setCliente(String cliente) {
        this.cliente.set(cliente);
    }

    public StringProperty clienteProperty() {
        return cliente;
    }

    public String getServico() {
        return servico.get();
    }

    public void setServico(String servico) {
        this.servico.set(servico);
    }

    public StringProperty servicoProperty() {
        return servico;
    }

    public String getData() {
        return data.get();
    }

    public void setData(String data) {
        this.data.set(data);
    }

    public StringProperty dataProperty() {
        return data;
    }

    public String getHorario() {
        return horario.get();
    }

    public void setHorario(String horario) {
        this.horario.set(horario);
    }

    public StringProperty horarioProperty() {
        return horario;
    }
}