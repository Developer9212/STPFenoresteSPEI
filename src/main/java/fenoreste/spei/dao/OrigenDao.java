package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fenoreste.spei.entity.Origen;

public interface OrigenDao extends JpaRepository<Origen,Integer> {
   
	@Query(value = "SELECT * FROM origenes WHERE matriz = 0 LIMIT 1",nativeQuery =  true)
	Origen buscarMatriz();
	
}
