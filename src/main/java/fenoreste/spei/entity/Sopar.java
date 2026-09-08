package fenoreste.spei.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="sopar")
@Data
public class Sopar implements Serializable {
      
	 @EmbeddedId
	 private PersonaPK personaPK;
	 private Integer idusuario;
	 private String tipo         ;
	 private String departamento ;
	 private String puesto       ;


	
	private static final long serialVersionUID = 1L;
}
