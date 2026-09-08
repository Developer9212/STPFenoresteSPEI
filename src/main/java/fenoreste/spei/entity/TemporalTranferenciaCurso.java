package fenoreste.spei.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "transferencias_spei_curso")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TemporalTranferenciaCurso implements Serializable {

    @EmbeddedId
    private TransferenciaCursoPK pk;
    private double monto;
    private boolean ok_saicoop;

    private static final long serialVersionUID = 1L;

}
