package fenoreste.spei.modelos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenPagoWS implements Serializable {
    protected Integer causaDevolucion;
    protected String claveCatUsuario1;
    protected String claveCatUsuario2;
    protected String clavePago;
    protected String claveRastreo;
    protected String claveRastreoDevolucion;
    protected String conceptoPago;
    protected String conceptoPago2;
    protected String cuentaBeneficiario;
    protected String cuentaBeneficiario2;
    protected String cuentaOrdenante;
    protected Integer digitoIdentificadorBeneficiario;
    protected Integer digitoIdentificadorOrdenante;
    protected String emailBeneficiario;
    protected String empresa;
    protected String error;
    protected String estado;
    protected String fechaLimitePago;
    protected Integer fechaOperacion;
    protected String firma;
    protected String folioOrigen;
    protected String folioPlataforma;
    protected String idCliente;
    protected Integer idEF;
    protected Integer institucionContraparte;
    protected Integer institucionOperante;
    protected BigDecimal iva;
    protected Integer medioEntrega;
    protected String monto;
    protected BigDecimal montoComision;
    protected BigDecimal montoInteres;
    protected BigDecimal montoOriginal;
    protected String nombreBeneficiario;
    protected String nombreBeneficiario2;
    protected String nombreOrdenante;
    protected String numCelularBeneficiario;
    protected String numCelularOrdenante;
    protected Integer pagoComision;
    protected Integer prioridad;
    protected String referenciaCobranza;
    protected Integer referenciaNumerica;
    protected Integer reintentos;
    protected String rfcCurpBeneficiario;
    protected String rfcCurpBeneficiario2;
    protected String rfcCurpOrdenante;
    protected String serieCertificado;
    protected String swift1;
    protected String swift2;
    protected Integer tipoCuentaBeneficiario;
    protected Integer tipoCuentaBeneficiario2;
    protected Integer tipoCuentaOrdenante;
    protected Integer tipoOperacion;
    protected Integer tipoPago;
    protected String topologia;
    protected Long tsAcuseBanxico;
    protected Long tsAcuseConfirmacion;
    protected Long tsCaptura;
    protected Long tsConfirmacion;
    protected Long tsDevolucion;
    protected Long tsDevolucionRecibida;
    protected Long tsEntrega;
    protected Long tsLiquidacion;
    protected String uetr;
    protected String usuario;
    protected String nombreParticipanteIndirecto;
    protected String cuentaParticipanteIndirecto;
    protected String rfcParticipanteIndirecto;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int codigo;

    private static final long serialVersionUID = 1L;
}
