package fenoreste.spei.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity(name = "temporal")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistroTemporal implements Serializable {

    @EmbeddedId
    private TemporalPk temporalPk;
    private Integer idorigenp;
    private Integer idproducto;
    private Integer idauxiliar;
    private boolean esentrada;
    private Double acapital;
    private String idcuenta;
    private boolean aplicado;
    private String concepto_mov;

    private static final long serialVersionUID = 1L;
}
