package fenoreste.spei.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;


@Entity
@Table(name="clave_instituciones")
@Data
public class Banco implements Serializable{
   
	@Id
	private Integer idbanco;
	private String nombre;
	private static final long serialVersionUID = 1L;
}
