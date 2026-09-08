package fenoreste.spei.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PersonaPK implements Serializable{
    
	private Integer idorigen;
	private Integer idgrupo;
	private Integer idsocio;
	
	private static final long serialVersionUID = 1L;
}
