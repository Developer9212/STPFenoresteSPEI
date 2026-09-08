package fenoreste.spei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.TablaDao;
import fenoreste.spei.entity.Tabla;
import fenoreste.spei.entity.TablaPK;

@Service
public class TablaServiceImpl implements ITablaService{
	
	@Autowired
	private TablaDao tablaDao;

	@Override
	public Tabla buscarPorId(TablaPK pk) {
		return tablaDao.findById(pk).orElse(null);
	}

}
