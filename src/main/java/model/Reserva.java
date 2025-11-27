package model;

public class Reserva {

    private static int contadorId = 1;

    private int id;
    private String cliente;
    private String servico;
    private String data;
    private String horario;

    public Reserva() {} 

    public Reserva(String cliente, String servico, String data, String horario) {
        this.id = contadorId++;
        this.cliente = cliente;
        this.servico = servico;
        this.data = data;
        this.horario = horario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public static void setProximoId(int novoId) { contadorId = novoId; }
}