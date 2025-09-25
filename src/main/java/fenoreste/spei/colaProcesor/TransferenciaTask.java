package fenoreste.spei.colaProcesor;

import fenoreste.spei.entity.Auxiliar;
import fenoreste.spei.modelos.request;

import java.util.concurrent.CompletableFuture;

public class TransferenciaTask {
    private final Auxiliar opa;
    private final request abono;
    private final int tipoOp;
    private final int cargoAbono;
    private final CompletableFuture<Integer> future;

    public TransferenciaTask(Auxiliar opa, request abono, int tipoOp, int cargoAbono, CompletableFuture<Integer> future) {
        this.opa = opa;
        this.abono = abono;
        this.tipoOp = tipoOp;
        this.cargoAbono = cargoAbono;
        this.future = future;
    }

    public Auxiliar getOpa() { return opa; }
    public request getRequest() { return abono; }
    public int getTipoOp() { return tipoOp; }
    public int getCargoAbono() { return cargoAbono; }
    public CompletableFuture<Integer> getFuture() {
        return future;
    }
}
