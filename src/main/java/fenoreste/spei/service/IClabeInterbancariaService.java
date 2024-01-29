package fenoreste.spei.service;

import fenoreste.spei.entity.AuxiliarPK;
import fenoreste.spei.entity.ClabeInterbancaria;

public interface IClabeInterbancariaService {
   
	public ClabeInterbancaria buscarPorId(AuxiliarPK pk);
	public ClabeInterbancaria buscarPorClabe(String clabe);
}
