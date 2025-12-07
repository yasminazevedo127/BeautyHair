package repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import model.Reserva; 

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReservaRepository {

    private static final ReservaRepository instance = new ReservaRepository();
    private static final DateTimeFormatter CUSTOM_DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter CUSTOM_TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public static ReservaRepository getInstance() {
        return instance;
    }

    private final String FILE_PATH = "reservas.json";
    private final List<Reserva> lista = new ArrayList<>();
    
    private final Gson gson = new GsonBuilder()
    	    .setPrettyPrinting()
    	    
    	    .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> 
    	        context.serialize(src.format(CUSTOM_DATE_FMT))) 
    	    .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> 
    	        LocalDate.parse(json.getAsString(), CUSTOM_DATE_FMT))
    	        
    	    .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) -> 
    	        context.serialize(src.format(CUSTOM_TIME_FMT))) 
    	    .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) -> 
    	        LocalTime.parse(json.getAsString(), CUSTOM_TIME_FMT))
    	    .create();


    private ReservaRepository() { carregarJson(); }

    public List<Reserva> getAll() { 
        return new ArrayList<>(lista); 
    }

    public void add(Reserva r) {
        if (r.getId() == 0) { 
            r.setId(Reserva.getContadorId());
            Reserva.setProximoId(Reserva.getContadorId() + 1);
        }
        
        lista.add(r);
        salvarJson();
    }

    
    public void update(int id, Reserva original) {
        lista.stream()
            .filter(r -> r.getId() == id)
            .findFirst()
            .ifPresent(r -> {
                
                r.setCliente(original.getCliente());
                r.setServico(original.getServico());
                r.setProfissional(original.getProfissional());
                r.setStatus(original.getStatus());
                r.setData(original.getData()); 
                r.setHorario(original.getHorario());
                salvarJson();
            });
    }
   

    public void removeById(int id) {
        lista.removeIf(r -> r.getId() == id);
        salvarJson();
    }

    
    public List<Reserva> search(String termo) {
        String termoFinal = termo == null ? "" : termo.toLowerCase().trim();
        
        if (termoFinal.isEmpty()) {
            return getAll();
        }

        return lista.stream()
                .filter(r -> r.contemTermo(termoFinal)) 
                .collect(Collectors.toList());
    }

    private void salvarJson() {
        try (FileWriter w = new FileWriter(FILE_PATH)) {
            gson.toJson(lista, w);
        } catch (Exception e) { 
            System.err.println("ERRO IO: Falha ao salvar arquivo JSON de reservas. " + e.getMessage());
        }
    }

    private void carregarJson() {
        if (!Files.exists(Paths.get(FILE_PATH))) {
             System.out.println("Arquivo JSON de reservas não encontrado. Iniciando vazio.");
             Reserva.setProximoId(1);
             return;
        }

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
            System.err.println("ERRO JSON: Falha ao carregar/deserializar reservas. " + e.getMessage());
            lista.clear();
            Reserva.setProximoId(1);
        }
    }
}