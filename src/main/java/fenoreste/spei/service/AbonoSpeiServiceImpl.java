package fenoreste.spei.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.AbonoSpeiDao;
import fenoreste.spei.entity.AbonoSpei;

@Service
public class AbonoSpeiServiceImpl implements IAbonoSpeiService{
    
	@Autowired
	private AbonoSpeiDao abonoSpeiDao;
	
	@Override
	public AbonoSpei buscarPorId(Integer id) {
		return abonoSpeiDao.findById(id).orElse(null);
	}

	@Override
	public List<AbonoSpei> todasPorFecha(Integer fecha) {
		return abonoSpeiDao.findByfechaOperacionAndAplicado(fecha,true);
	}

	@Override
	public void guardar(AbonoSpei abono) {
      abonoSpeiDao.save(abono);
	}

}
