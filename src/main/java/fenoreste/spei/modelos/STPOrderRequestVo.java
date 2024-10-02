package fenoreste.spei.modelos;

import java.io.Serializable;

import lombok.Data;

@Data
public class STPOrderRequestVo implements Serializable{
	
	private String claveRastreo;
	private String conceptoPago;
	private String cuentaOrdenante;
	private String cuentaBeneficiario;
	private String empresa;
	private String institucionContraparte;
	private String institucionOperante;
	private String monto;
	private String nombreBeneficiario;
	private String nombreOrdenante;
	private String referenciaNumerica;
	private String rfcCurpBeneficiario;
	private String rfcCurpOrdenante;
	private String tipoCuentaBeneficiario;
	private String tipoCuentaOrdenante;
	private String tipoPago;
	private String firma;
	
	private static final long serialVersionUID = 1L;
}
