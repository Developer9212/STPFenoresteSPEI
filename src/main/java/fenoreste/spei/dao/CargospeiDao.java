package fenoreste.spei.dao;

import fenoreste.spei.entity.CargoSpei;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CargospeiDao extends JpaRepository<CargoSpei,Integer> {

      @Query(value = "SELECT (CASE WHEN sum(amount) > 0 " +
              " THEN sum(amount) ELSE 0 END) FROM transferencias_bankingly" +
              " WHERE clientbankidentifier=?1 AND subtransactiontypeid='3' AND" +
              " transactiontypeid='1' AND to_char(fechaejecucion,'yyyymm')=?2",nativeQuery = true)
      public Double totalEnviadoMes(String ogs,String fecha);

    @Query(value = "SELECT (CASE WHEN SUM(monto) IS NULL THEN 0 ELSE SUM(monto) END) FROM speirecibido WHERE (SELECT SUBSTRING (fechaoperacion::::TEXT FROM 1 FOR 6)) = ?2 AND cuentabeneficiario= ?1 AND aplicado=true",nativeQuery = true)
    public Double totalMes(String clabe,String periodo);
}
