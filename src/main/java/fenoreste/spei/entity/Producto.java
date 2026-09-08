package fenoreste.spei.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="productos")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Producto implements Serializable{

	@Id
	@Column(name="idproducto")
	private Integer idproducto;
	private String nombre;
	private Integer idorigen;
	private String cuentaaplica;
	private String cuentavencida;
	private String cuentaintord;
	private String cuentaidnc;
	private String cuentaidncv;
	private String cuentaidncres;
	private String cuentaoiod;
	private String cuentaintmor;
	private String cuentaiva;
	private String cuentarc;
	private String cuentari;
	private Integer tipoproducto;
	private Integer tiporetiro;             
	private Integer tipocalculo;
	private Integer tasaio;	 
	private Integer tasaiod;	 
	private Integer tasaim;
	private Integer iva;
	private Integer ivaim;	 
	private Integer garantias;	 
	private Integer avales;
	private Boolean reqsocio;
	private Integer tipoamortizacion;
	private Integer maxeventos;
	private Integer maxdv;
	private String saldominimo;
	private String saldomaximo;
	private String cuentageprcc;
	private String cuentageprci;
	private String cuentaeprcc;
	private String cuentaeprci;
	private String cuentaoimd;
	private String cuentaoima;
	private String cuentaoioa;
	private String cuentaivaim;
	private String cuentaivaidncvig;
	private String cuentaivaidncven;
	private String cuentaivappidnc;
	private String cuentaintordv;
	private Integer plazomax;
	private Integer tipofinalidad;
	private String activo;
	private String pagodiafijo;
	private String cant_aperturas;
	private Integer producto_padre;
	private Integer tasasp;
	private Integer ivasp;
	private String cuentasp;
	private String cuentaivasp;
	private String tolerancia_im;
	private String tolerancia_com_no_pago;
	private Double monto_com_no_pago;
	private Double comision_apertura;
	private String rango_edad;
	private String evalua_aperturas;
	private String cuentaintmorv;
	private String cuentaidncmres;
	private String cuentaidncm;
	private String cuentaidncmv;
	private String cuentageprcim;
	private String cuentaeprcim; 
	
	private static final long serialVersionUID = 1L;
		
}
