package fenoreste.spei.service;

import org.springframework.stereotype.Service;

import fenoreste.spei.entity.Persona;
import fenoreste.spei.entity.PersonaPK;



@Service
public interface IPersonaService {
	
	public Persona buscarPorId(PersonaPK pk);
	//AplicaMitras
	public Persona buscarPorCurpGrupo(String curp,Integer idgrupo);
}
 