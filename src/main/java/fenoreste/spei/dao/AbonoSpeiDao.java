package fenoreste.spei.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.AbonoSpei;

public interface AbonoSpeiDao extends JpaRepository<AbonoSpei,Integer>{
   
	List<AbonoSpei> findByfechaOperacionAndAplicado(Integer fecha,boolean aplicado);
}
