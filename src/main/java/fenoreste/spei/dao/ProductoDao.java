package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.Producto;

public interface ProductoDao extends JpaRepository<Producto,Integer> {

}
