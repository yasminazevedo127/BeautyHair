package services;

import model.Reserva;
import repository.ReservaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaValidator {
	
	private final ReservaRepository repo = ReservaRepository.getInstance();

    public List<String> validar(Reserva r) {
        List<String> erros = new ArrayList<>();
        
        if (r.getCliente() == null || r.getCliente().trim().isEmpty()) {
            erros.add("O nome do cliente é obrigatório.");
        }
        if (r.getProfissional() == null || r.getProfissional().trim().isEmpty()) {
            erros.add("O profissional é obrigatório.");
        }
        if (r.getServico() == null || r.getServico().trim().isEmpty()) {
            erros.add("O serviço é obrigatório.");
        }
        if (r.getDataAsLocalDate() == null) {
            erros.add("A data da reserva é obrigatória.");
        }
        if (r.getHorarioAsLocalTime() == null) {
            erros.add("O horário da reserva é obrigatório.");
        }
        
        if (r.getDataAsLocalDate() != null && r.getDataAsLocalDate().isBefore(java.time.LocalDate.now())) {
            erros.add("Não é permitido agendar para datas passadas.");
        }
        
        try {
            LocalDate data = r.getDataAsLocalDate();
            LocalTime horario = r.getHorarioAsLocalTime();
            
            if (data == null) {
                erros.add("Escolha uma data válida.");
            } else if (horario == null) {
                erros.add("Escolha um horário válido.");
            } else {
                LocalDate hoje = LocalDate.now();
                LocalTime agora = LocalTime.now();
                if (data.isBefore(hoje) || (data.isEqual(hoje) && horario.isBefore(agora))) {
                    erros.add("A data e horário escolhidos já passaram.");
                }
            }
        } catch (Exception e) {
            erros.add("Formato de data/hora inválido."); 
        }
        
        return erros;
    }
    
    public boolean existeConflito(Reserva novaReserva, int idParaExcluir) {
        if (novaReserva.getDataAsLocalDate() == null || novaReserva.getHorarioAsLocalTime() == null) {
            return false; 
        }

        return repo.getAll().stream()
                .filter(reservaExistente -> reservaExistente.getId() != idParaExcluir)
                .anyMatch(reservaExistente -> reservaExistente.conflitaCom(novaReserva));
    }
    
    public boolean existeConflito(Reserva novaReserva) {
        return existeConflito(novaReserva, 0); 
    }
}
