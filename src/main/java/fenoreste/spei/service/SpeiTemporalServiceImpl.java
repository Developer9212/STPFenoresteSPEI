package fenoreste.spei.service;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.SpeiTemporalDao;
import fenoreste.spei.entity.SpeiTemporal;

@Service
public class SpeiTemporalServiceImpl implements ISpeiTemporalService{
    
	@Autowired
	private SpeiTemporalDao speiTemporalDao;
    
	@Override
	public void guardar(SpeiTemporal mov) {
		speiTemporalDao.save(mov);
	}

	@Override
	@Transactional
	public void eliminar(String sesion,Integer referencia) {
		/*List<SpeiTemporal>todasAplicado = speiTemporalDao.todasAplicado(sesion,String.valueOf(referencia));
		for(int i = 0;i<todasAplicado.size();i++) {
			SpeiTemporal spei = todasAplicado.get(i);
			speiTemporalDao.delete(spei);  
		}*/
	}
	
	

}
