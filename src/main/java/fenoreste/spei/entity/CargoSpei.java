package fenoreste.spei.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

//@Entity
//@Table(name="speirecibido")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CargoSpei implements Serializable {

    private String institucionContraparte ;
    private String empresa                ;
    private String claveRastreo       ;
    private String institucionOperante;
    private String monto;
    private String tipoPago;
    private String tipoCuentaOrdenante;
    private String nombreOrdenante    ;
    private String cuentaOrdenante    ;
    private String rfcCurpOrdenante           ;
    private String tipoCuentaBeneficiario     ;
    private String nombreBeneficiario         ;
    private String cuentaBeneficiario         ;
    private String rfcCurpBeneficiario        ;
    private String conceptoPago               ;
    private String referenciaNumerica         ;
    private String nombreParticipanteIndirecto;
    private String cuentaParticipanteIndirecto;
    private String rfcParticipanteIndirecto   ;

    private static final long serialVersionUID = 1L;

}
