package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.AuxiliarPK;
import fenoreste.spei.entity.FolioTarjeta;

public interface FolioTarjetaDao extends JpaRepository<FolioTarjeta, AuxiliarPK>{

}
