/*
  * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fenoreste.spei.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Wilmer
 */
@Entity
@Table(name = "auxiliares")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Auxiliar implements Serializable{      
    @EmbeddedId 
    private AuxiliarPK auxiliarPK; 
    private Integer idorigen;
    private Integer idgrupo;
    private Integer idsocio;
    private Date fechaape;
    private Integer elaboro;
    private Integer autorizo;
    private Short estatus;
    private BigDecimal tasaio;
    private BigDecimal tasaim;
    private BigDecimal tasaiod;
    private BigDecimal montosolicitado;
    private BigDecimal montoautorizado;
    private BigDecimal montoprestado;
    private Integer idfinalidad;
    private short plazo;
    private short periodoabonos;
    private BigDecimal saldoinicial;
    private BigDecimal saldo;
    private BigDecimal io;
    private BigDecimal idnc;
    private BigDecimal ieco;
    private BigDecimal im;
    private BigDecimal iva;
    @Temporal(TemporalType.DATE)
    private Date fechaactivacion;
    @Temporal(TemporalType.DATE)
    private Date fechaumi;
    private String idnotas;
    private short tipoprestamo;
    private String cartera;
    private BigDecimal contaidnc;
    private BigDecimal contaieco;
    private Short tipoamortizacion;
    private BigDecimal saldodiacum;
    @Temporal(TemporalType.DATE)
    private Date fechacartera;
    @Temporal(TemporalType.DATE)
    private Date fechauma;
    private BigDecimal ivaidnccalc;
    private BigDecimal ivaidncpag;
    private Short tiporeferencia;
    private Integer calificacion;
    private Short pagodiafijo;
    private BigDecimal iodif;
    private BigDecimal garantia;
    private BigDecimal saldodiacummi;
    private BigDecimal comision;
    @Temporal(TemporalType.DATE)
    private Date fechasdiacum;
    private BigDecimal comision_np;
    private Boolean pagos_dia_ultimo;
    @Temporal(TemporalType.DATE)
    private Date fecha_solicitud;
    @Temporal(TemporalType.DATE)
    private Date fecha_autorizacion;
    private BigDecimal idncm;    
    private BigDecimal iecom;    

	private static final long serialVersionUID = 1L;
}
