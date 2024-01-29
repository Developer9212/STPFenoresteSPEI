package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.Persona;
import fenoreste.spei.entity.PersonaPK;



public interface PersonaDao extends JpaRepository<Persona,PersonaPK> {
      
}
