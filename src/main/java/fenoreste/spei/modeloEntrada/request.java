package fenoreste.spei.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class request {

	 private Integer id;
	 private Integer fechaOperacion;
	 private Integer institucionOrdenante;
	 private Integer institucionBeneficiaria;
	 private String claveRastreo;
	 private Double monto;
	 private String nombreOrdenante;
	 private Integer tipoCuentaOrdenante;
	 private String cuentaOrdenante;
	 private String rfcCurpOrdenante;
	 private String nombreBeneficiario;
	 private Integer tipoCuentaBeneficiario;
	 private String cuentaBeneficiario;
	 private String nombreBeneficiario2;
	 private Integer tipoCuentaBeneficiario2;
	 private String cuentaBeneficiario2;
	 private String rfcCurpBeneficiario;
	 private String conceptoPago;
	 private Integer referenciaNumerica;
	 private String empresa;
	 private Integer tipoPago;
	 private String tsLiquidacion;
	 private String folioCodi;
	 
	
	 
	 
}
