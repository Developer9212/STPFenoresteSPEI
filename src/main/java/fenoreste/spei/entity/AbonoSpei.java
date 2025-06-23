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
	 private Integer  referenciaNumerica;
	 private String  empresa;
	 private Date fechaentrada;
	 private Integer responsecode;
	 private String mensaje_core;
	 private boolean aplicado;
	 private Date fechaProcesada;
	 private String tsliquidacion;
	 private boolean retardo;
	 private boolean stp_ok;
	 
	 private static final long serialVersionUID = 1L;
}
