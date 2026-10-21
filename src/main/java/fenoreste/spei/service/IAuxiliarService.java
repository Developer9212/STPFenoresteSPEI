package fenoreste.spei.service;

import fenoreste.spei.entity.Auxiliar;
import fenoreste.spei.entity.AuxiliarPK;
import fenoreste.spei.entity.PersonaPK;

public interface IAuxiliarService {
  
	public Auxiliar buscarPorId(AuxiliarPK pk);
	public Auxiliar buscarPorOgsProducto(PersonaPK personaPK,Integer idProducto);
}
