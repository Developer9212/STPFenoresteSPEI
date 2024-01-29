package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.Usuario;

public interface UsuarioDao extends JpaRepository<Usuario,Integer> {

}
