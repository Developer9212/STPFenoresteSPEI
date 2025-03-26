package fenoreste.spei.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="speirecibido_duplicados")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AbonoSpeiDuplicado implements Serializable {

    @EmbeddedId
    private AbonoSpeiPK abonoSpeiPK;
    private Integer fechaOperacion;
    private Integer  institucionOrdenante;
    private Integer institucionBeneficiaria;
    private Double  monto;
    private String  nombreOrdenante;
    private String  cuentaOrdenante;
    private String  nombreBeneficiario;
    private String  cuentaBeneficiario;
    private String  conceptoPago;
    private Integer  referenciaNumerica;
    private Date fechaentrada;
    private Integer responsecode;
    private String mensaje_core;

    private static final long serialVersionUID = 1L;
}
