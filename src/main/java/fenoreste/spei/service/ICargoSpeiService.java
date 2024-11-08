package fenoreste.spei.service;

import fenoreste.spei.entity.CargoSpei;

public interface ICargoSpeiService {

    public CargoSpei buscarCargoSpei(Integer idOrden);
    public CargoSpei guardarCargoSpei(CargoSpei cargo);

}
