package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.AuxiliarPK;
import fenoreste.spei.entity.ClabeInterbancaria;
import org.springframework.data.jpa.repository.Query;

public interface ClabeInterbancariaDao  extends JpaRepository<ClabeInterbancaria,AuxiliarPK>{
	
	public ClabeInterbancaria findByClabe(String clable);

	@Query(value= " SELECT w.*  FROM ws_siscoop_clabe_interbancaria w INNER JOIN ws_siscoop_clabe cb USING(clabe)" +
			      "  WHERE w.clabe = ?1 AND cb.fecha_vencimiento > (select distinct fechatrabajo from origenes limit 1)",nativeQuery = true)
	public ClabeInterbancaria clabeInterbancariaActiva(String clabe);

}
