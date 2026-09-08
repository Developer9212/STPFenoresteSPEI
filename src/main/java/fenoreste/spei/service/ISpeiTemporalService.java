package fenoreste.spei.service;

import fenoreste.spei.entity.SpeiTemporal;
import fenoreste.spei.entity.SpeiTemporalPK;

public interface ISpeiTemporalService {
    
	public void guardar(SpeiTemporal mov);
	public void eliminar(String sesion,String referencia);
	public void eliminarTodos();
	public SpeiTemporal buscarPorId(SpeiTemporalPK pk);
	public Integer totalTemporales(Integer idorigen,Integer idgrupo,Integer idsocio,String referecia,double acapital);
}
