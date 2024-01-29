package fenoreste.spei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.BancoDao;
import fenoreste.spei.entity.Banco;

@Service
public class BancoServiceImplements implements IBancoService{
    
	@Autowired
	private BancoDao bancoDao;
	
	@Override
	public Banco buscarPorId(Integer id) {
		return bancoDao.findById(id).orElse(null);
	}

	@Override
	public Banco buscarNombre(String noombre) {
		return bancoDao.findByNombre(noombre);
	}

}
