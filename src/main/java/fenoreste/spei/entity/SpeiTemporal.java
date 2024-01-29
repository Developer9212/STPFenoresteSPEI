package fenoreste.spei.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="spei_entrada_temporal_cola_guardado")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpeiTemporal implements Serializable{
	 
	   @Id
	   @GeneratedValue(generator="sec_spei_temporal") 
	   @SequenceGenerator(name="sec_spei_temporal",sequenceName="sec_spei_temporal", allocationSize=1)
	   private Integer id;	    
       private boolean aplicado;    
	   private Integer idusuario;   
	   private String sesion;       
	   private Integer idorigen;    
	   private Integer idgrupo;     
	   private Integer idsocio;     
	   private Integer idorigenp = 0;
	   private Integer idproducto = 0;
	   private Integer idauxiliar = 0;  
	   private boolean esentrada;   
	   private Double acapital;     
	   private Double io_pag = 0.0;       
	   private Double io_cal = 0.0;  
	   private Double im_pag = 0.0;       
	   private Double im_cal = 0.0;           
	   private Double aiva = 0.0;             
	   private Double saldodiacum = 0.0;      
	   private Double abonifio = 0.0;  
	   private String idcuenta = "0";         
	   private Double ivaio_pag = 0.0;        
	   private Double ivaio_cal = 0.0;        
	   private Double ivaim_pag = 0.0;        
	   private Double ivaim_cal = 0.0;        
	   private Integer mov = 0;             
	   private Integer tipomov = 0;
	   private String referencia;
	   private Integer diasvencidos = 0;
	   private Double montovencido = 0.0;
	   private Integer idorigena = 0;
	   private boolean huella_valida;
	   private String concepto_mov;
	   private String fe_nom_archivo;
	   private String fe_xml;
	   private String sai_aux = "";
	  private String poliza_generada;
	   private Date fecha_aplicado;
	   private Integer tipopoliza;
	   
	private static final long serialVersionUID = 1L;

}
