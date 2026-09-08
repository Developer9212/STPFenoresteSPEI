package fenoreste.spei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.OrigenDao;
import fenoreste.spei.entity.Origen;

@Service
public class OrigenServiceImpl implements IOrigenService{
     
	@Autowired
	private OrigenDao origenDao;
	
	@Override
	public Origen buscarPorId(Integer id) {
		return origenDao.findById(id).orElse(null);
	}

	@Override
	public Origen buscarMatriz() {
		return origenDao.buscarMatriz();
	}

}
