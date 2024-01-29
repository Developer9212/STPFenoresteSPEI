package fenoreste.spei.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fenoreste.spei.entity.SpeiTemporal;

public interface SpeiTemporalDao extends JpaRepository<SpeiTemporal,Long>{
   
	@Query(value = "SELECT * FROM spei_entrada_temporal_cola_guardado WHERE sesion=?1 and referencia=?2",nativeQuery = true)
	List<SpeiTemporal> todasAplicado(String sesion,String referencia);
}
