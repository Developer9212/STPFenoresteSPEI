package fenoreste.spei.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="speirecibido")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AbonoSpei implements Serializable {
	
	 @Id
	 @Column(name="id", nullable = false, unique = true)
	 private Integer id;
	 private Integer fechaOperacion;
	 private Integer  institucionOrdenante;
	 private Integer institucionBeneficiaria;
	 private String  claveRastreo;
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
	 private String mensajeerror;
	 private boolean aplicado;
	 private Date fechaProcesada;
	 
	 private static final long serialVersionUID = 1L;
}
