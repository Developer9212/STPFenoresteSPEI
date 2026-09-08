package fenoreste.spei.service;

import fenoreste.spei.entity.PersonaPK;
import fenoreste.spei.entity.Sopar;

public interface ISoparService {

	public Sopar buscarPorIdTipo(PersonaPK pk,String tipo);
}
