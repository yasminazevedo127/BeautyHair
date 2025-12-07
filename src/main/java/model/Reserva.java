package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Reserva {

    private static int contadorId = 1;

    private int id;
    private String cliente;
    private String servico;
    private String profissional;
    private String data;
    private String horario;
    private String status;

    public Reserva() {} 

    public Reserva(String cliente, String servico, String profissional, String data, String horario, String status) {
        this.cliente = cliente;
        this.profissional = profissional;
        this.servico = servico;
        this.status = status;
        this.data = data;
        this.horario = horario;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }
    
    public String getProfissional() { return profissional; }
    public void setProfissional(String profissional) { this.profissional = profissional; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
    
    public static int getContadorId(){ return contadorId; }
    public static void setProximoId(int novoId) { contadorId = novoId; }
    
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public LocalDate getDataAsLocalDate() {
        if (data== null || data.isBlank()) return null;
        return LocalDate.parse(data, DATE_FMT);
    }

    public LocalTime getHorarioAsLocalTime() {
        if (horario == null || horario.isBlank()) return null;
        return LocalTime.parse(horario, TIME_FMT);
    }
    
    public boolean conflitaCom(Reserva outra) {
        if (outra == null) return false;

        if (this.profissional == null || outra.profissional == null) return false;
        
        LocalDate thisData = this.getDataAsLocalDate();
        LocalTime thisHorario = this.getHorarioAsLocalTime();
        LocalDate outraData = outra.getDataAsLocalDate();
        LocalTime outraHorario = outra.getHorarioAsLocalTime();

        if (thisData == null || thisHorario == null || outraData == null || outraHorario == null) {
            return false; 
        }
        
        return this.profissional.equals(outra.profissional) &&
               thisData.equals(outraData) &&
               thisHorario.equals(outraHorario);
    }

    public boolean contemTermo(String termo) {
        return (getCliente() != null && getCliente().toLowerCase().contains(termo)) ||
               (getServico() != null && getServico().toLowerCase().contains(termo)) ||
               (getProfissional() != null && getProfissional().toLowerCase().contains(termo)) ||
               (getData() != null && getData().toLowerCase().contains(termo)) ||
               (getHorario() != null && getHorario().toLowerCase().contains(termo));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reserva reserva = (Reserva) o;
        return id == reserva.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}