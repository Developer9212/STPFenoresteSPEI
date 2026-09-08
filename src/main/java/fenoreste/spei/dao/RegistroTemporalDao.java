package fenoreste.spei.dao;

import fenoreste.spei.entity.RegistroTemporal;
import fenoreste.spei.entity.TemporalPk;
import fenoreste.spei.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface RegistroTemporalDao extends JpaRepository<RegistroTemporal, TemporalPk> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM temporal WHERE idusuario=?1 AND sesion = ?2 AND referencia=?3",nativeQuery = true)
    int eliminarTemporal(Integer idusuario, String sesion, String referencia);
}
