package fenoreste.spei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.FolioTarjetaDao;
import fenoreste.spei.entity.AuxiliarPK;
import fenoreste.spei.entity.FolioTarjeta;

@Service
public class FolioTarjetaServiceImpl implements IFolioTarjetaService{

	@Autowired
	private FolioTarjetaDao folioTarjetaDao;
	
	@Override
	public FolioTarjeta buscarPorId(AuxiliarPK pk) {
	   return folioTarjetaDao.findById(pk).orElse(null);
	}

}
