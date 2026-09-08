package fenoreste.spei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.AuxiliarDao;
import fenoreste.spei.entity.Auxiliar;
import fenoreste.spei.entity.AuxiliarPK;

@Service
public class AuxiliarServiceImpl implements IAuxiliarService{
    
	@Autowired
	private AuxiliarDao auxiliarDao;
	
	@Override
	public Auxiliar buscarPorId(AuxiliarPK pk) {
		return auxiliarDao.findById(pk).orElse(null);
	}

}
