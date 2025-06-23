package fenoreste.spei.service;


import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import fenoreste.spei.entity.SpeiTemporalPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.SpeiTemporalDao;
import fenoreste.spei.entity.SpeiTemporal;

@Service
public class SpeiTemporalServiceImpl implements ISpeiTemporalService{
    
	@Autowired
	private SpeiTemporalDao speiTemporalDao;

	@Autowired
	private EntityManager entityManager;
    
	@Override
	@Transactional
	@Modifying
	public void guardar(SpeiTemporal mov) {

		SpeiTemporal nuevo = new SpeiTemporal();
		SpeiTemporalPK pk = new SpeiTemporalPK();

		SpeiTemporal existente = entityManager.find(SpeiTemporal.class, mov.getSpeiTemporalPK());
		if (existente != null) {
			entityManager.detach(existente); // Desvincula el objeto viejo si existe
		}

		pk.setIdorigenp(mov.getSpeiTemporalPK().getIdorigenp());
		pk.setIdproducto(mov.getSpeiTemporalPK().getIdproducto());
		pk.setIdauxiliar(mov.getSpeiTemporalPK().getIdauxiliar());
		pk.setReferencia(mov.getSpeiTemporalPK().getReferencia());
		pk.setIdoperacion(mov.getSpeiTemporalPK().getIdoperacion());

		nuevo.setSpeiTemporalPK(pk);
		nuevo.setAcapital(mov.getAcapital());
		nuevo.setEsentrada(mov.isEsentrada());
		nuevo.setIdgrupo(mov.getIdgrupo());
		nuevo.setIdorigen(mov.getIdorigen());
		nuevo.setIdusuario(mov.getIdusuario());
		nuevo.setIdsocio(mov.getIdsocio());
		nuevo.setSesion(mov.getSesion());
		nuevo.setMov(mov.getMov());
		nuevo.setIdcuenta(mov.getIdcuenta());
		nuevo.setConcepto_mov(mov.getConcepto_mov());
		nuevo.setTipomov(mov.getTipomov());
		nuevo.setAplicado(mov.isAplicado());



        entityManager.merge(nuevo);
		//speiTemporalDao.save(nuevo);
	}

	@Override
	@Transactional
	@Modifying
	public void eliminar(String sesion,String referencia) {
		int eliminado = speiTemporalDao.eliminarRegistro(sesion,referencia);
	}

	@Override
	public void eliminarTodos() {
		speiTemporalDao.deleteAll();
	}

	@Override
	public SpeiTemporal buscarPorId(SpeiTemporalPK pk) {
		return speiTemporalDao.findById(pk).orElse(null);
	}

	@Override
	public Integer totalTemporales(Integer idorigen, Integer idgrupo, Integer idsocio, String referecia, double acapital) {
		List<SpeiTemporal> lista = speiTemporalDao.todasEnTemporal(idorigen,idgrupo,idsocio,referecia,acapital);
		return lista.size();
	}


}
