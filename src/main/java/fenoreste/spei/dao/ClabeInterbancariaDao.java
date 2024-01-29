package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.AuxiliarPK;
import fenoreste.spei.entity.ClabeInterbancaria;

public interface ClabeInterbancariaDao  extends JpaRepository<ClabeInterbancaria,AuxiliarPK>{
	
	ClabeInterbancaria findByClabe(String clable);

}
