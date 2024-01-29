package fenoreste.spei.service;

import fenoreste.spei.entity.Usuario;

public interface IUsuarioService {

	public Usuario guardar(Usuario user);
	public Usuario buscar(Integer id);
	public void eliminar(Usuario user);
	public Usuario modificar(Usuario user); 
}
