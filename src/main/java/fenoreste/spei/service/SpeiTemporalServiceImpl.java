package fenoreste.spei.service;


import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.SpeiTemporalDao;
import fenoreste.spei.entity.SpeiTemporal;

@Service
public class SpeiTemporalServiceImpl implements ISpeiTemporalService{
    
	@Autowired
	private SpeiTemporalDao speiTemporalDao;
    
	@Override
	@Transactional
	@Modifying
	public void guardar(SpeiTemporal mov) {
		speiTemporalDao.save(mov);
	}

	@Override
	@Transactional
	@Modifying
	public void eliminar(String sesion,String referencia) {
		List<SpeiTemporal>todasAplicado = speiTemporalDao.todasAplicado(sesion,String.valueOf(referencia));
		System.out.println("total aplicados:"+todasAplicado);
		for(int i = 0;i<todasAplicado.size();i++) {
			SpeiTemporal spei = todasAplicado.get(i);
			speiTemporalDao.delete(spei);
		}
	}

	@Override
	public void eliminarTodos() {
		speiTemporalDao.deleteAll();
	}


}
