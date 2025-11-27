package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReservaFX {

    private final int id;
    private final StringProperty cliente = new SimpleStringProperty();
    private final StringProperty servico = new SimpleStringProperty();
    private final StringProperty data = new SimpleStringProperty();
    private final StringProperty horario = new SimpleStringProperty();

    public ReservaFX(int id, String cliente, String servico, String data, String horario) {
        this.id = id;
        setCliente(cliente);
        setServico(servico);
        setData(data);
        setHorario(horario);
    }

    public int getId() { return id; }

    public String getCliente() { return cliente.get(); }
    public void setCliente(String c) { cliente.set(c); }
    public StringProperty clienteProperty() { return cliente; }

    public String getServico() { return servico.get(); }
    public void setServico(String s) { servico.set(s); }
    public StringProperty servicoProperty() { return servico; }

    public String getData() { return data.get(); }
    public void setData(String d) { data.set(d); }
    public StringProperty dataProperty() { return data; }

    public String getHorario() { return horario.get(); }
    public void setHorario(String h) { horario.set(h); }
    public StringProperty horarioProperty() { return horario; }

    public static ReservaFX fromReserva(Reserva r) {
        return new ReservaFX(r.getId(), r.getCliente(), r.getServico(), r.getData(), r.getHorario());
    }

    public Reserva toReserva() {
        Reserva r = new Reserva(getCliente(), getServico(), getData(), getHorario());
        r.setId(id);
        return r;
    }
}