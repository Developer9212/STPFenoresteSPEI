package fenoreste.spei.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fenoreste.spei.entity.Auxiliar;
import fenoreste.spei.entity.AuxiliarPK;
import org.springframework.data.jpa.repository.Query;

public interface AuxiliarDao extends JpaRepository<Auxiliar,AuxiliarPK>{

    @Query(value = "SELECT * FROM auxiliares WHERE idorigenp= ?1 AND idproducto =?2 aND idauxiliar=?3 ORDER BY idorigenp desc LIMIT 1",nativeQuery = true)
    public Auxiliar buscaAuxiliar(int idorigenp,int idproducto,int idauxiliar);

    @Query(value = "SELECT * FROM auxiliares WHERE idorigenp=?1 AND idgrupo=?2 AND idsocio=?3 AND idproducto=?4 and estatus = 2 ",nativeQuery = true)
    public Auxiliar buscarPorOgsProducto(int idorigenp,int idgrupo,int idsocio,int idproducto);
}
