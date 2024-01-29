package fenoreste.spei.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ws_siscoop_clabe_interbancaria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClabeInterbancaria implements Serializable{
     
	 @EmbeddedId
	 AuxiliarPK auxPk;
	 private String  clabe;
	 @Temporal(TemporalType.TIMESTAMP)
	 private Date fecha_hora; 
	 private boolean asignada;
	 private boolean activa;
	 private boolean bloqueada;
	 
	 private static final long serialVersionUID = 1L;

}
