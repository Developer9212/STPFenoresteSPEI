package fenoreste.spei.service;

import fenoreste.spei.dao.EstadoSpeiDao;
import fenoreste.spei.entity.EstadoSpei;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstadoSpeiServiceImpl implements IEstadoSpeiService{

    @Autowired
    private EstadoSpeiDao estadoSpeiDao;

    @Override
    public EstadoSpei buscarPorId(Integer id) {
        return estadoSpeiDao.findById(id).orElse(null);
    }

    @Override
    public void guardar(EstadoSpei estadoSpei) {
          estadoSpeiDao.save(estadoSpei);
    }
}
