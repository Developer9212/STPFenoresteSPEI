package fenoreste.spei.colaProcesor;

import fenoreste.spei.entity.Auxiliar;
import fenoreste.spei.modelos.request;
import fenoreste.spei.service.InServiceGeneral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TransferenciaQueueProcessor {

   private final BlockingQueue<TransferenciaTask> queue = new LinkedBlockingQueue<>();

    @Autowired
    private InServiceGeneral transferenciaService;

    @PostConstruct
    public void startProcessing() {
        Thread worker = new Thread(() -> {
            while (true) {
                TransferenciaTask task = null; // <-- Declaración fuera del try
                try {
                    task = queue.take(); // <-- Puede lanzar InterruptedException

                    int resultado = transferenciaService.realizarTransferencia(
                            task.getOpa(),
                            task.getRequest(),
                            task.getTipoOp(),
                            task.getCargoAbono()
                    );

                    task.getFuture().complete(resultado);
                } catch (Exception e) {
                    if (task != null) {
                        task.getFuture().completeExceptionally(e);
                    }
                    e.printStackTrace(); // Opcional: loguear el error
                }
            }
        });
        worker.setDaemon(true);
        worker.start();
    }


    public CompletableFuture<Integer> enqueue(
            Auxiliar opa,
            request abono,
            int tipoop,
            int cargoabono
    ) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        queue.offer(new TransferenciaTask(opa, abono, tipoop, cargoabono, future));
        return future;
    }
}
