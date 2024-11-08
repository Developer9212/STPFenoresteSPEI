package fenoreste.spei.service;

import fenoreste.spei.dao.CargospeiDao;
import fenoreste.spei.entity.CargoSpei;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CargoSpeiServiceImpl implements ICargoSpeiService{

    @Autowired
    private CargospeiDao cargospeiDao;

    @Override
    public CargoSpei buscarCargoSpei(Integer idOrden) {
        return null;
    }

    @Override
    public CargoSpei guardarCargoSpei(CargoSpei cargo) {
        return cargospeiDao.save(cargo);
    }
}
