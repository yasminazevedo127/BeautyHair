package repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken; 
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServicosRepository {

    private static final ServicosRepository instance = new ServicosRepository();
    
    private final String ARQUIVO = "servicos.json";
    private List<String> servicos;
    private final Gson gson = new Gson();

    private ServicosRepository() {
        servicos = new ArrayList<>();
        carregar();
    }

    public static ServicosRepository getInstance() {
        return instance;
    }

    public List<String> getAll() {
        return new ArrayList<>(servicos);
    }

    public void add(String servico) {
        if (servico != null && !servico.trim().isEmpty()) {
            servicos.add(servico.trim());
            salvar();
        }
    }

    public void remove(String servico) {
        servicos.remove(servico);
        salvar();
    }

    
    private void carregar() {
        if (!Files.exists(Paths.get(ARQUIVO))) {
            servicos = new ArrayList<>();
            return;
        }
        
        try {
            String conteudo = Files.readString(Paths.get(ARQUIVO));
            
            if (conteudo.trim().isEmpty()) {
                servicos = new ArrayList<>();
                return;
            }

            Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            
            List<String> loadedList = gson.fromJson(conteudo, listType);
            
            if (loadedList != null) {
                servicos = loadedList;
            } else {
                servicos = new ArrayList<>();
            }

        } catch (IOException e) {
            System.err.println("ERRO IO: Falha ao carregar o arquivo " + ARQUIVO + ". " + e.getMessage());
            servicos = new ArrayList<>();
        } catch (Exception e) {
            System.err.println("ERRO JSON: Falha ao deserializar o conteúdo de " + ARQUIVO + ". " + e.getMessage());
            servicos = new ArrayList<>();
        }
    }

 
    private void salvar() {
        try {
            String jsonContent = gson.toJson(servicos);

            Files.writeString(Paths.get(ARQUIVO), jsonContent);
            
        } catch (IOException e) {
            System.err.println("ERRO IO: Falha ao salvar no arquivo " + ARQUIVO + ". " + e.getMessage());
        }
    }
}