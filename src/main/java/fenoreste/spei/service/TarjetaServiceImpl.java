package fenoreste.spei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.TarjetaDao;
import fenoreste.spei.entity.Tarjeta;

@Service
public class TarjetaServiceImpl implements ITarjetaService {
	
	@Autowired
	private TarjetaDao tarjetaDao;

	@Override
	public Tarjeta buscarPorId(String idtarjeta) {
		return tarjetaDao.findById(idtarjeta).orElse(null);
	}
	
	

}
