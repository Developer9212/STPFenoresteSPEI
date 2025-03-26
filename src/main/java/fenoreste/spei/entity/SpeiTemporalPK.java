package fenoreste.spei.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigInteger;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor

public class SpeiTemporalPK implements Serializable {

    private Integer idorigenp;
    private Integer idproducto;
    private Integer idauxiliar;
    private boolean esentrada;
    private BigInteger idoperacion;

    private static final long serialVersionUID = 1L;

}
