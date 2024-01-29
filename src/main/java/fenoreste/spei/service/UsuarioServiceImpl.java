package fenoreste.spei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.UsuarioDao;
import fenoreste.spei.entity.Usuario;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

	@Autowired
	private UsuarioDao usuarioDao;
	
	@Override
	public Usuario guardar(Usuario user) {		
		return usuarioDao.save(user);
	}

	@Override
	public Usuario buscar(Integer id) {
		return usuarioDao.findById(id).orElse(null);
	}

	@Override
	public void eliminar(Usuario user) {		
		 usuarioDao.delete(user);
	}

	@Override
	public Usuario modificar(Usuario user) {
		return usuarioDao.save(user);
	}

}
