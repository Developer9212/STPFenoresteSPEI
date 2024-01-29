package fenoreste.spei.modelos;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConcPeticionVo implements Serializable{

      private String empresa;
	  private Integer fecha;
      private String firma;
	  private Integer page;
	  private String tipoOrden;
	  
	  
	  private static final long serialVersionUID = 1L;
	
	
}
