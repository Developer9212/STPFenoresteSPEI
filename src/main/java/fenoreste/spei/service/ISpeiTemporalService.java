package fenoreste.spei.service;

import fenoreste.spei.entity.SpeiTemporal;

public interface ISpeiTemporalService {
    
	public void guardar(SpeiTemporal mov);
	public void eliminar(String sesion,Integer referencia);
}
