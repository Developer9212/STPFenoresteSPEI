package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fenoreste.spei.entity.Persona;
import fenoreste.spei.entity.PersonaPK;



public interface PersonaDao extends JpaRepository<Persona,PersonaPK> {
	
	@Query(value = "SELECT * FROM personas WHERE curp=?1 AND idgrupo=?2",nativeQuery = true)
	public Persona findByCurpIdgrupo(String curp,Integer idgrupo);
      
}
