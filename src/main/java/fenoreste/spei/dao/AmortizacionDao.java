package fenoreste.spei.dao;

import fenoreste.spei.entity.Amortizacion;
import fenoreste.spei.entity.AuxiliarPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AmortizacionDao extends JpaRepository<Amortizacion,AuxiliarPK> {

    @Query(value = "SELECT * FROM amortizaciones WHERE idorigenp=?1 AND idproducto=?2 AND idauxiliar=?3 AND todopag=false ORDER BY vence ASC LIMIT 1",nativeQuery = true)
    public Amortizacion buscarSiguienteAmortizacion(Integer idorigenp, Integer idproducto, Integer idauxiliar);

}
