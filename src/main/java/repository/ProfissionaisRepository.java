package repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProfissionaisRepository {

    private static ProfissionaisRepository instance;

    private final List<String> profissionais = new ArrayList<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final String FILE_PATH = "profissionais.json"; // <-- arquivo na raiz da execução

    private ProfissionaisRepository() {
        carregar();
    }

    public static ProfissionaisRepository getInstance() {
        if (instance == null) {
            instance = new ProfissionaisRepository();
        }
        return instance;
    }

    public List<String> getAll() {
        return new ArrayList<>(profissionais);
    }

    public void add(String nome) {
        profissionais.add(nome);
        salvar();
    }

    public void remove(String nome) {
        profissionais.remove(nome);
        salvar();
    }

    private void carregar() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            salvar(); 
            return;
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {

            Type type = new TypeToken<ProfissionaisJson>(){}.getType();
            ProfissionaisJson data = gson.fromJson(reader, type);

            if (data != null && data.profissionais != null) {
                profissionais.clear();
                profissionais.addAll(data.profissionais);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void salvar() {
        try {
            ProfissionaisJson obj = new ProfissionaisJson();
            obj.profissionais = profissionais;

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(FILE_PATH), StandardCharsets.UTF_8)) {
                gson.toJson(obj, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ProfissionaisJson {
        List<String> profissionais;
    }
}