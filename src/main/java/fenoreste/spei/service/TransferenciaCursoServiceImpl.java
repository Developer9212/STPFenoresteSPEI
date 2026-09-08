package fenoreste.spei.service;


import fenoreste.spei.dao.TransferenciasCursoDao;
import fenoreste.spei.entity.TemporalTranferenciaCurso;
import fenoreste.spei.entity.TransferenciaCursoPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TransferenciaCursoServiceImpl implements ITransferenciaCursoService {

    @Autowired
    private TransferenciasCursoDao transferenciaCursoDao;

    @Override
    public TemporalTranferenciaCurso buscarPorId(TransferenciaCursoPK pk) {
        return transferenciaCursoDao.findById(pk).orElse(null);
    }

    @Override
    @Transactional
    public TemporalTranferenciaCurso guardarMovimiento(TemporalTranferenciaCurso temp) {
        return transferenciaCursoDao.save(temp);
    }

    @Override
    @Transactional
    public void eliminar() {
        transferenciaCursoDao.deleteAll();
    }


}
