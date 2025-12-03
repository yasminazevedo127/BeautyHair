package repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Reserva;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservaRepository {

    private static ReservaRepository instance;

    public static ReservaRepository getInstance() {
        if (instance == null) instance = new ReservaRepository();
        return instance;
    }

    private final String FILE_PATH = "reservas.json";
    private final List<Reserva> lista = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private ReservaRepository() { carregarJson(); }

    public List<Reserva> getAll() { return lista; }

    public void add(Reserva r) {
        lista.add(r);
        salvarJson();
    }

    public void update(int id, Reserva original) {
        for (Reserva r : lista) {
            if (r.getId() == id) {

                r.setCliente(original.getCliente());
                r.setServico(original.getServico());
                r.setProfissional(original.getProfissional());
                r.setStatus(original.getStatus());
                r.setData(original.getData());
                r.setHorario(original.getHorario());

                salvarJson();
                return;
            }
        }
    }
    
    public void updateStatus(int id, String novoStatus) {
        for (Reserva r : lista) {
            if (r.getId() == id) {
                r.setStatus(novoStatus);
                salvarJson();
                return;
            }
        }
    }

    public void removeById(int id) {
        lista.removeIf(r -> r.getId() == id);
        salvarJson();
    }

    public List<Reserva> search(String termo) {
        termo = termo == null ? "" : termo.toLowerCase();
        List<Reserva> out = new ArrayList<>();
        if (termo.isEmpty()) { out.addAll(lista); return out; }
        for (Reserva r : lista) {
            if ((r.getCliente() != null && r.getCliente().toLowerCase().contains(termo)) ||
                (r.getServico() != null && r.getServico().toLowerCase().contains(termo)) ||
                (r.getProfissional() != null && r.getProfissional().toLowerCase().contains(termo)) ||
                (r.getData() != null && r.getData().toLowerCase().contains(termo)) ||
                (r.getHorario() != null && r.getHorario().toLowerCase().contains(termo))) {
                out.add(r);
            }
        }
        return out;
    }

    private void salvarJson() {
        try (FileWriter w = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, w);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void carregarJson() {
        try (FileReader r = new FileReader(FILE_PATH)) {
            Type tipo = new TypeToken<List<Reserva>>(){}.getType();
            List<Reserva> carregada = gson.fromJson(r, tipo);
            if (carregada != null) {
                lista.clear();
                lista.addAll(carregada);
                int maior = lista.stream().mapToInt(Reserva::getId).max().orElse(0);
                Reserva.setProximoId(maior + 1);
            }
        } catch (Exception e) {
            System.out.println("Arquivo JSON não encontrado ou vazio.");
        }
    }
    
    public boolean existeReserva(LocalDate data, LocalTime hora) {
        if (data == null || hora == null) return false;
        Optional<Reserva> existe = lista.stream()
                .filter(r -> {
                    if (r.getDataAsLocalDate() == null || r.getHorarioAsLocalTime() == null) return false;
                    return r.getDataAsLocalDate().equals(data) && r.getHorarioAsLocalTime().equals(hora);
                })
                .findAny();
        return existe.isPresent();
    }
}