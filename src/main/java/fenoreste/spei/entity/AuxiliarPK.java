package fenoreste.spei.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuxiliarPK implements Serializable {
	
	private static final long serialVersionUID = 1L;
     
	 @Column(name = "idorigenp", nullable = false)
    private Integer idorigenp;
	
	 @Column(name = "idproducto",nullable = false)
    private Integer idproducto;
	
	@Column(name = "idauxiliar" , nullable = false)
    private Integer idauxiliar;
    

    
   
}
