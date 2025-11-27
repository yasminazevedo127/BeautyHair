package repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServicosRepository {

    private static ServicosRepository instance;
    private final String ARQUIVO = "servicos.json";
    private List<String> servicos;

    private ServicosRepository() {
        servicos = new ArrayList<>();
        carregar();
    }

    public static ServicosRepository getInstance() {
        if (instance == null) {
            instance = new ServicosRepository();
        }
        return instance;
    }

    public List<String> getAll() {
        return new ArrayList<>(servicos);
    }

    public void add(String servico) {
        servicos.add(servico);
        salvar();
    }

    public void remove(String servico) {
        servicos.remove(servico);
        salvar();
    }

    private void carregar() {
        try {
            if (!Files.exists(Paths.get(ARQUIVO))) {
                servicos = new ArrayList<>();
                return;
            }

            String conteudo = new String(Files.readAllBytes(Paths.get(ARQUIVO)));
            conteudo = conteudo.trim();
            servicos = new ArrayList<>();

            if (!conteudo.isEmpty() && conteudo.startsWith("[") && conteudo.endsWith("]")) {
                conteudo = conteudo.substring(1, conteudo.length() - 1); // remove [ e ]
                if (!conteudo.isBlank()) {
                    String[] items = conteudo.split(",");
                    for (String item : items) {
                        servicos.add(item.trim().replaceAll("^\"|\"$", "")); // remove aspas
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            servicos = new ArrayList<>();
        }
    }

    private void salvar() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            writer.write("[");
            for (int i = 0; i < servicos.size(); i++) {
                writer.write("\"" + servicos.get(i) + "\"");
                if (i < servicos.size() - 1) writer.write(",");
            }
            writer.write("]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}