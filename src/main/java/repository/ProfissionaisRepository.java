package repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProfissionaisRepository {

    private static final ProfissionaisRepository instance = new ProfissionaisRepository();

    private final List<String> profissionais = new ArrayList<>();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final String FILE_PATH = "profissionais.json";

    private ProfissionaisRepository() {
        carregar();
    }

    public static ProfissionaisRepository getInstance() {
        return instance;
    }

    public List<String> getAll() {
        return new ArrayList<>(profissionais);
    }

    public void add(String nome) {
        if (nome != null && !nome.trim().isEmpty()) {
            profissionais.add(nome.trim());
            salvar();
        }
    }

    public void remove(String nome) {
        profissionais.remove(nome);
        salvar();
    }

    private void carregar() {
    	java.nio.file.Path path = Paths.get(FILE_PATH);

        if (!Files.exists(path) || !Files.isReadable(path) || path.toFile().length() == 0) {
            profissionais.clear();
            return;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Type type = new TypeToken<List<String>>(){}.getType();
            List<String> loadedList = gson.fromJson(reader, type);

            if (loadedList != null) {
                profissionais.clear();
                profissionais.addAll(loadedList);
            }

        } catch (Exception e) {
            System.err.println("ERRO: Falha ao carregar profissionais do JSON. " + e.getMessage());
            profissionais.clear();
        }
    }

    private void salvar() {
        try {
            String jsonContent = gson.toJson(profissionais);
            
            Files.write(Paths.get(FILE_PATH), jsonContent.getBytes(StandardCharsets.UTF_8));
        
        } catch (Exception e) {
            System.err.println("ERRO: Falha ao salvar profissionais no JSON. " + e.getMessage());
        }
    }
}