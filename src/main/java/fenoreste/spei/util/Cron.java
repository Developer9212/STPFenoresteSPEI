package fenoreste.spei.util;


import fenoreste.spei.service.ISpeiTemporalService;
import fenoreste.spei.service.ITransferenciaCursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class Cron {

    @Autowired
    private ITransferenciaCursoService transferenciaCursoService;

    @Autowired
    private ISpeiTemporalService speiTemporalService;


    @Async
    @Scheduled(cron = "0 00 03 * * ?", zone = "America/Monterrey")
    public void executeTask() {
        //System.out.println("Ejecutando tarea a la 04 AM");
        // Lógica de la tarea a ejecutar
        HiloParaEliminarCache();
    }


    private void HiloParaEliminarCache() {
        try {
            System.out.println("::::::::::::Vamos a eliminar cache temporales:::::::::::::::::::::::");
            transferenciaCursoService.eliminar();
            speiTemporalService.eliminarTodos();

        } catch (Exception e) {
            System.out.println("Error al eliminar caches tablas temporales :" + e.getMessage());
        }


    }


    private Date convierteStringDate(String fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaP = new Date();
        try {
            fechaP = formato.parse(fecha);
            System.out.println("Fecha formateada:" + fechaP.toString());
        } catch (Exception e) {
            System.out.println("Error al convertir: " + e.getMessage());
        }
        return fechaP;
    }

}


