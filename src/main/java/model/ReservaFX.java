package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.*;

public class ReservaFX {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty cliente = new SimpleStringProperty();
    private final StringProperty servico = new SimpleStringProperty();
    private final StringProperty profissional = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    private final ObjectProperty<LocalDate> data = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> horario = new SimpleObjectProperty<>();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public ReservaFX() {}

    public ReservaFX(Reserva r) {
        if (r == null) return;

        this.id.set(r.getId());
        this.cliente.set(r.getCliente());
        this.servico.set(r.getServico());
        this.profissional.set(r.getProfissional());
        this.status.set(r.getStatus() == null || r.getStatus().isBlank() ? "Agendada" : r.getStatus());

        // Data
        if (r.getData() != null && !r.getData().isBlank()) {
            try {
                this.data.set(LocalDate.parse(r.getData(), DATE_FMT));
            } catch (Exception e) {
                this.data.set(LocalDate.parse(r.getData()));
            }
        }

        if (r.getHorario() != null && !r.getHorario().isBlank()) {
            try {
                this.horario.set(LocalTime.parse(r.getHorario(), TIME_FMT));
            } catch (Exception e) {
                this.horario.set(LocalTime.parse(r.getHorario()));
            }
        }
    }

    public static ReservaFX fromReserva(Reserva r) {
        return new ReservaFX(r);
    }

    public Reserva toReserva() {
        Reserva r = new Reserva();

        r.setId(getId());
        r.setCliente(getCliente());
        r.setServico(getServico());
        r.setProfissional(getProfissional());

        r.setStatus(getStatus()); 

        if (data.get() != null) r.setData(data.get().format(DATE_FMT));
        if (horario.get() != null) r.setHorario(horario.get().format(TIME_FMT));

        return r;
    }

    public int getId() { return id.get(); }
    public void setId(int v) { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getCliente() { return cliente.get(); }
    public void setCliente(String v) { cliente.set(v); }
    public StringProperty clienteProperty() { return cliente; }

    public String getServico() { return servico.get(); }
    public void setServico(String v) { servico.set(v); }
    public StringProperty servicoProperty() { return servico; }

    public String getProfissional() { return profissional.get(); }
    public void setProfissional(String v) { profissional.set(v); }
    public StringProperty profissionalProperty() { return profissional; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { status.set(v); }
    public StringProperty statusProperty() { return status; }

    public LocalDate getData() { return data.get(); }
    public void setData(LocalDate v) { data.set(v); }
    public ObjectProperty<LocalDate> dataProperty() { return data; }

    public LocalTime getHorario() { return horario.get(); }
    public void setHorario(LocalTime v) { horario.set(v); }
    public ObjectProperty<LocalTime> horarioProperty() { return horario; }
}