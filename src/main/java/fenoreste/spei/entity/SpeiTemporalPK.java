package fenoreste.spei.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor

public class SpeiTemporalPK implements Serializable {

    private Integer idorigenp;
    private Integer idproducto;
    private Integer idauxiliar;
    private String referencia;
    private BigInteger idoperacion;


        // Getters, Setters

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SpeiTemporalPK)) return false;
            SpeiTemporalPK that = (SpeiTemporalPK) o;
            return Objects.equals(idorigenp, that.idorigenp) && Objects.equals(idproducto, that.idproducto) && Objects.equals(idauxiliar, that.idauxiliar)
                    && Objects.equals(referencia, that.referencia) && Objects.equals(idoperacion, that.idoperacion);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idorigenp,idproducto,idauxiliar,referencia,idoperacion);
        }




    private static final long serialVersionUID = 1L;

}
