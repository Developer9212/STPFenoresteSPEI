package fenoreste.spei.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="speirecibido")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AbonoSpei implements Serializable {
	
	 @EmbeddedId
	 private AbonoSpeiPK abonoSpeiPK;
	 private Integer fechaOperacion;
	 private Integer  institucionOrdenante;
	 private Integer institucionBeneficiaria;
	 private Double  monto;
	 private String  nombreOrdenante;
	 private Integer  tipocuentaOrdenante;
	 private String  cuentaOrdenante;
	 private String  rfccurpOrdenante;
	 private String  nombreBeneficiario;
	 private Integer tipocuentaBeneficiario;
	 private String  cuentaBeneficiario;
	 private String  rfcCurpBeneficiario;
	 private String  conceptoPago;
	 private Integer  referenciaNumerica=0;
	 private String  empresa="";
	 private Date fechaentrada=new Date();
	 private Integer responsecode=0;
	 private String mensaje_core="";
	 private boolean aplicado = false;
	 private Date fechaProcesada;
	 private String tsliquidacion;
	 private boolean retardo = false;
	 private boolean stp_ok = false;
	 
	 private static final long serialVersionUID = 1L;
}
