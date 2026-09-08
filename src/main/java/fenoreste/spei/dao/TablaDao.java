package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.Tabla;
import fenoreste.spei.entity.TablaPK;

public interface TablaDao extends JpaRepository<Tabla,TablaPK> {

}
