package fenoreste.spei.modelos;

import java.io.Serializable;

import lombok.Data;

@Data
public class Conciliacion implements Serializable {

	private Integer idEF;
	private String claveRastreo;
	private String claveRastreoDevolucion;
	private String conceptoPago;
	private String cuentaBeneficiario;
	private String cuentaOrdenante;
	private String empresa;
	private String estado;
	private Integer fechaOperacion;
	private Integer institucionContraparte;
	private Integer institucionOperante;
	private Integer medioEntrega;
	private Double monto;
	private String nombreBeneficiario;
	private String nombreOrdenante;
	private String nombreCep;
	private String rfcCep;
	private String sello;
	private String rfcCurpBeneficiario;
	private Integer referenciaNumerica;
	private String rfcCurpOrdenante;
	private Integer tipoCuentaBeneficiario;
	private Integer tipoCuentaOrdenante;
	private Integer tipoPago;
	private Integer tsCaptura;
	private Integer tsLiquidacion;
	private String causaDevolucion;
	private String urlCEP;
	
	private static final long serialVersionUID = 1L;
}
