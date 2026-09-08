package fenoreste.spei.modeloSalida;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenPagoWS implements Serializable {
    protected Integer institucionContraparte;
    protected String empresa;
    protected String fechaOperacion;
    protected String folioOrigen   ;
    protected String claveRastreo;
    protected Integer institucionOperante;
    protected String monto;
    protected Integer tipoPago;
    protected Integer tipoCuentaOrdenante;
    protected String nombreOrdenante;
    protected String cuentaOrdenante;
    protected String rfcCurpOrdenante;
    protected Integer tipoCuentaBeneficiario;
    protected String nombreBeneficiario;
    protected String cuentaBeneficiario;
    protected String rfcCurpBeneficiario;
    protected String emailBeneficiario;
    protected String tipoCuentaBeneficiario2;
    protected String nombreBeneficiario2;
    protected String cuentaBeneficiario2;
    protected String rfcCurpBeneficiario2;
    protected String conceptoPago;
    protected String conceptoPago2;
    protected String claveCatUsuario1;
    protected String claveCatUsuario2;
    protected String clavePago;
    protected String referenciaCobranza;
    protected Integer referenciaNumerica;
    protected String tipoOperacion;
    protected String topologia;
    protected String usuario;
    protected String medioEntrega;
    protected String prioridad;
    protected String iva;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int codigo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String error;

    private static final long serialVersionUID = 1L;
}
