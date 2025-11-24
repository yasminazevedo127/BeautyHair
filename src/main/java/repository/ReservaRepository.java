package repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import model.Reserva;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReservaRepository {

    private final String FILE_PATH = "reservas.json";
    private final List<Reserva> lista = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ReservaRepository() {
        carregarJson();
    }

    public List<Reserva> getAll() {
        return lista;
    }

    public void add(Reserva r) {
        lista.add(r);
        salvarJson();
    }

    public void update(int id, Reserva nova) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == id) {
                lista.set(i, nova);
                salvarJson();
                return;
            }
        }
    }

    public void removerPorId(int id) {
        lista.removeIf(r -> r.getId() == id);
        salvarJson();
    }

    private void salvarJson() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarJson() {
        try (FileReader reader = new FileReader(FILE_PATH)) {

            Type tipoLista = new TypeToken<List<Reserva>>() {}.getType();
            List<Reserva> carregada = gson.fromJson(reader, tipoLista);

            if (carregada != null) {
                lista.clear();
                lista.addAll(carregada);

                int maiorId = lista.stream()
                        .mapToInt(Reserva::getId)
                        .max()
                        .orElse(0);
                model.Reserva.setProximoId(maiorId + 1);
            }

        } catch (IOException e) {
            System.out.println("Nenhum arquivo JSON encontrado. Um novo será criado.");
        }
    }
}