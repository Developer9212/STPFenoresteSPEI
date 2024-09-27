package fenoreste.spei.modelos;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ConcPeticionVo implements Serializable{

      private String empresa;
	  @JsonInclude(value = JsonInclude.Include.NON_NULL)
	  private Integer fechaOperacion;
      private String firma;
	  private Integer page;
	  private String tipoOrden;
	  
	  
	  private static final long serialVersionUID = 1L;
	
	
}
