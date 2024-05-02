package fenoreste.spei.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.PersonaDao;
import fenoreste.spei.entity.Persona;
import fenoreste.spei.entity.PersonaPK;

@Service
public class PersonaServiceImpl implements IPersonaService{
	
	@Autowired
	private PersonaDao personaRepository;

	@Override
	public Persona buscarPorId(PersonaPK pk) {
		return personaRepository.getById(pk);
	}

	@Override
	public Persona buscarPorCurpGrupo(String curp, Integer idgrupo) {
		return personaRepository.findByCurpIdgrupo(curp, idgrupo);
	}
	
	
	
	
	

}
