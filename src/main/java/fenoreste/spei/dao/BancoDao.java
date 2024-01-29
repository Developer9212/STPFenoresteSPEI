package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.Banco;

public interface BancoDao extends JpaRepository<Banco,Integer>{

	public Banco findByNombre(String nombre);
}
