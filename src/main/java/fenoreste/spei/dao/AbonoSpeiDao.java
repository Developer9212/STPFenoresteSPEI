package fenoreste.spei.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.AbonoSpei;
import org.springframework.data.jpa.repository.Query;

public interface AbonoSpeiDao extends JpaRepository<AbonoSpei,Integer>{
   
	public List<AbonoSpei> findByfechaOperacionAndAplicado(Integer fecha,boolean aplicado);

	@Query(value = "SELECT (CASE WHEN SUM(monto) IS NULL THEN 0 ELSE SUM(monto) END) FROM speirecibido WHERE LEFT(fechaoperacion::::TEXT, 6) = ?2 AND cuentabeneficiario= ?1 AND aplicado=true",nativeQuery = true)
	public Double totalMes(String clabe,String periodo);
}
