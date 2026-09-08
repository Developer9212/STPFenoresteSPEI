package fenoreste.spei.modelos;

import java.io.Serializable;

import lombok.Data;

@Data
public class STPOrderRequestVo implements Serializable{

	private String cuentaBeneficiario;
	private String tipoCuentaOrdenante;
	private String nombreBeneficiario;
	private String rfcCurpBeneficiario;
	private String conceptoPago;
	private String institucionOperante;
	private String referenciaNumerica;
	private String claveRastreo;
	private String monto;
	private String tipoCuentaBeneficiario;
	private String institucionContraparte;
	private String tipoPago;
	private String cuentaOrdenante;
	private String empresa;
	private String latitud;
	private String longitud;
	private String firma;

	
	
	private static final long serialVersionUID = 1L;
}
