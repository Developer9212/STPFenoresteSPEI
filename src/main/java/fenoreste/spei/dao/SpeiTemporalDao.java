package fenoreste.spei.dao;


import java.util.List;

import fenoreste.spei.entity.SpeiTemporalPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import fenoreste.spei.entity.SpeiTemporal;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.transaction.Transactional;

public interface SpeiTemporalDao extends JpaRepository<SpeiTemporal, SpeiTemporalPK>{



	@Query(value = "SELECT * FROM spei_entrada_temporal_cola_guardado WHERE idusuario =?1 AND sesion=?2 and referencia=?3",nativeQuery = true)
	public List<SpeiTemporal> todasAplicado(Integer idusuario,String sesion,String referencia);

	@Query(value = "SELECT * FROM spei_entrada_temporal_cola_guardado WHERE idorigen = ?1 and idgrupo=?2 and idsocio=?3 and referencia=?4 and acapital=?5",nativeQuery = true)
	public List<SpeiTemporal> todasEnTemporal(Integer idorigen,Integer idgrupo,Integer idsocio,String referencia,double acapital);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM spei_entrada_temporal_cola_guardado WHERE sesion = ?1 AND referencia = ?2",nativeQuery = true)
	int eliminarRegistro(String sesion,String referencia);
	
}
