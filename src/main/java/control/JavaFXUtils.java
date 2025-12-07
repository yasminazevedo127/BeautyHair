package control;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JavaFXUtils {

	public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
	public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public static void configurarDatePicker(DatePicker dateData) {
		dateData.setPromptText("dd/MM/yyyy");
	    dateData.setConverter(new StringConverter<LocalDate>() {
		    @Override
		    public String toString(LocalDate date) {
		        return date != null ? DATE_FMT.format(date) : "";
		    }
		
		    @Override
		    public LocalDate fromString(String string) {
		    	if (string == null || string.isBlank()) return null;
		             
		    	try {
		    		return LocalDate.parse(string, DATE_FMT);
		        } catch (java.time.format.DateTimeParseException e) {
		                 return null; 
		        }
		    }
	    });
	}

	public static void carregarHorarios(ComboBox<LocalTime> comboHorario) {
		List<LocalTime> horarios = new ArrayList<>();
	    LocalTime inicio = LocalTime.of(8, 0);
	    LocalTime fim = LocalTime.of(18, 0);
	    while (!inicio.isAfter(fim)) {
	        horarios.add(inicio);
	        inicio = inicio.plusMinutes(30);
	    }
	    comboHorario.getItems().setAll(horarios);
	
	    comboHorario.setCellFactory(cb -> new ListCell<>() {
	        @Override
	        protected void updateItem(LocalTime item, boolean empty) {
	            super.updateItem(item, empty);
	            setText(empty || item == null ? null : item.format(TIME_FMT));
	        }
	    });
	     
	    comboHorario.setConverter(new StringConverter<LocalTime>() {
	        @Override
	        public String toString(LocalTime object) {
	            return object != null ? object.format(TIME_FMT) : "";
	        }
	
	        @Override
	        public LocalTime fromString(String string) {
	            return null;
	        }
	    });
	}
}
