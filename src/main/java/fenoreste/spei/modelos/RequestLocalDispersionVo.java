package fenoreste.spei.modelos;

import java.io.Serializable;

import lombok.Data;

@Data
public class RequestLocalDispersionVo implements Serializable{
    
	private String opaCliente;
	private Double monto;
	private String institucionContraparte;
	private String nombreBeneficiario;
	private String rfcCurpBeneficiario;
	private String conceptoPago;
	private String cuentaBeneficiario;
	
	private static final long serialVersionUID = 1L;
}
