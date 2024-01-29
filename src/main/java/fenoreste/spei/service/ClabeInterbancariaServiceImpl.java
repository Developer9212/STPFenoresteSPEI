package fenoreste.spei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.ClabeInterbancariaDao;
import fenoreste.spei.entity.AuxiliarPK;
import fenoreste.spei.entity.ClabeInterbancaria;

@Service
public class ClabeInterbancariaServiceImpl implements IClabeInterbancariaService{
    
	@Autowired
	private ClabeInterbancariaDao clabeInterbancariaDao;
	
	@Override
	public ClabeInterbancaria buscarPorId(AuxiliarPK pk) {
		return clabeInterbancariaDao.findById(pk).orElse(null);
	}

	@Override
	public ClabeInterbancaria buscarPorClabe(String clabe) {
		return clabeInterbancariaDao.findByClabe(clabe);
	}

}
