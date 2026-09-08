package fenoreste.spei.service;

import fenoreste.spei.entity.Banco;

public interface IBancoService {
   
	public Banco buscarPorId(Integer id);
	public Banco buscarNombre(String noombre);
}
