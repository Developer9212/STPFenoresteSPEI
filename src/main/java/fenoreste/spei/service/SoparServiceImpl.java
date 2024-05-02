package fenoreste.spei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.SoparDao;
import fenoreste.spei.entity.PersonaPK;
import fenoreste.spei.entity.Sopar;


@Service
public class SoparServiceImpl implements ISoparService {
    
	@Autowired
	private SoparDao soparDao;
	

	@Override
	public Sopar buscarPorIdTipo(PersonaPK pk, String tipo) {
		return soparDao.findByPersonaPKAndTipo(pk, tipo);
	}



}
