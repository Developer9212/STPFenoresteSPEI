package fenoreste.spei.modeloEntrada;

import java.io.Serializable;

import lombok.Data;

@Data
public class RequestLocalDispersionVo implements Serializable{
    
	private String opaCliente;
	private Double monto;
	private String banco;
	private String beneficiario;
	private String rfcCurpBeneficiario;
	private String conceptoPago;
	private String cuentaBeneficiario;
	private String location;
	
	private static final long serialVersionUID = 1L;
}
