package fenoreste.spei.service;

import fenoreste.spei.entity.CargoSpei;
import fenoreste.spei.entity.SpeiEnviadoPK;

public interface ICargoSpeiService {

    public CargoSpei buscarCargoSpei(SpeiEnviadoPK speiEnviadoPK);
    public CargoSpei guardarCargoSpei(CargoSpei cargo);

    public CargoSpei buscarPorIdOrdenClaveRastreo(Integer idorden,String claveRastreo);

}
