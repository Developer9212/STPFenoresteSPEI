package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.Persona;
import fenoreste.spei.entity.PersonaPK;
import fenoreste.spei.entity.Sopar;

public interface SoparDao extends JpaRepository<Sopar, PersonaPK> {
   
	public Sopar findByPersonaPKAndTipo(PersonaPK pk,String tipo);
}
