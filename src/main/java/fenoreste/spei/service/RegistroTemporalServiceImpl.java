package fenoreste.spei.service;

import fenoreste.spei.dao.RegistroTemporalDao;
import fenoreste.spei.entity.TemporalPk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RegistroTemporalServiceImpl implements IRegistroTemporalService {

    @Autowired
    private RegistroTemporalDao registroTemporalDao;

    @Override
    @Transactional
    @Modifying
    public void eliminarPorId(TemporalPk temporalPk) {
        int eliminado = registroTemporalDao.eliminarTemporal(temporalPk.getIdusuario(),temporalPk.getSesion(),temporalPk.getReferencia());
    }
}
