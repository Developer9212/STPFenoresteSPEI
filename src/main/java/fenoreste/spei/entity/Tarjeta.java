package fenoreste.spei.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ws_siscoop_tarjetas")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tarjeta implements Serializable {
    
	@Id
	private String idtarjeta;
	private Date fecha;
	private boolean  seleccionada;
	private boolean asignada;
	private boolean eliminada;
	@Temporal(TemporalType.DATE)
	private Date fecha_vencimiento; 


	private static final long serialVersionUID = 1L;

}
