package fenoreste.spei.dao;

import fenoreste.spei.entity.TemporalTranferenciaCurso;
import fenoreste.spei.entity.TransferenciaCursoPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferenciasCursoDao extends JpaRepository<TemporalTranferenciaCurso, TransferenciaCursoPK>{



}
