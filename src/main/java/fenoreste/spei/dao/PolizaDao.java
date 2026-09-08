package fenoreste.spei.dao;

import fenoreste.spei.entity.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PolizaDao extends JpaRepository<Poliza,Integer> {

    @Query(value = "SELECT count(*) FROM polizas WHERE concepto LIKE '%SPEI ENTRADA%' + ?1+'%'",nativeQuery = true)
    public Integer totalPolizas(String concepto);
}
