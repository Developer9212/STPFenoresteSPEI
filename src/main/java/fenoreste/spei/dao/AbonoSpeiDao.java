package fenoreste.spei.dao;

import java.util.List;

import fenoreste.spei.entity.AbonoSpeiPK;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import fenoreste.spei.entity.AbonoSpei;

public interface AbonoSpeiDao extends JpaRepository<AbonoSpei, AbonoSpeiPK>{
   
	@Query(value = "SELECT (CASE WHEN SUM(monto) IS NULL THEN 0 ELSE SUM(monto) END) from speirecibido WHERE fechaoperacion=?1 AND aplicado=true AND cuentabeneficiario=?2",nativeQuery = true)
	public Double montodiario(Integer fecha,String cuenta);

	@Query(value = "SELECT (CASE WHEN SUM(monto) IS NULL THEN 0 ELSE SUM(monto) END) FROM speirecibido WHERE (SELECT SUBSTRING (fechaoperacion::::TEXT FROM 1 FOR 6)) = ?2 AND cuentabeneficiario= ?1 AND aplicado=true",nativeQuery = true)
	public Double totalMes(String clabe,String periodo);
}
