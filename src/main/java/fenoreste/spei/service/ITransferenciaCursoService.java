package fenoreste.spei.service;

import fenoreste.spei.entity.TemporalTranferenciaCurso;
import fenoreste.spei.entity.TransferenciaCursoPK;

public interface ITransferenciaCursoService {

    public TemporalTranferenciaCurso buscarPorId(TransferenciaCursoPK pk);

    public TemporalTranferenciaCurso guardarMovimiento(TemporalTranferenciaCurso temp);

    public void eliminar();
}
