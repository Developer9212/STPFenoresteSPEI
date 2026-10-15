package fenoreste.spei.service;

import fenoreste.spei.dao.CargospeiDao;
import fenoreste.spei.entity.CargoSpei;
import fenoreste.spei.entity.SpeiEnviadoPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CargoSpeiServiceImpl implements ICargoSpeiService{

    @Autowired
    private CargospeiDao cargospeiDao;

    @Override
    public CargoSpei buscarCargoSpei(SpeiEnviadoPK speiEnviadoPK) {
        return cargospeiDao.findById(speiEnviadoPK).orElse(null);
    }

    @Override
    @Transactional
    public CargoSpei guardarCargoSpei(CargoSpei cargo) {
        return cargospeiDao.save(cargo);
    }

    @Override
    @Transactional
    public CargoSpei buscarPorIdOrdenClaveRastreo(Integer idOrden,String claveRastreo){
        return cargospeiDao.buscarPorIdOrdenclave(idOrden, claveRastreo);
    }
}
