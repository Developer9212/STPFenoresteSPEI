package fenoreste.spei;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Ejec {

	    public static void main(String[] args) {
	        // Define las dos fechas
	    	
	    	Date f1 = new Date();
	    	
	        LocalDateTime fecha1 =  f1.toInstant()
	                .atZone(ZoneId.systemDefault()) // Puedes usar otro ZoneId si lo prefieres
	                .toLocalDateTime();
	        
	        LocalDateTime fecha2 =  new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	        
	        // Calcula la duración entre las dos fechas
	        Duration duration = Duration.between(fecha1, fecha2);
	        
	        // Obtiene los segundos de la duración
	        long segundos = duration.getSeconds();
	        
	        // Imprime el resultado
	        System.out.println("Segundos entre las dos fechas: " + segundos);
	    }
	
}
