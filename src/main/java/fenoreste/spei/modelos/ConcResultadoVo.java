package fenoreste.spei.modelos;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class ConcResultadoVo implements Serializable {

	  private Integer estado;
	  private String mensaje;
	  @JsonInclude(value = Include.NON_NULL)
	  private List<Conciliacion> datos;
	  @JsonInclude(value = Include.NON_NULL)
	  private Integer total;
      
	  @JsonInclude(value = Include.NON_NULL)
 	  private Integer codigo;
	 
	private static final long serialVersionUID = 1L;
	
}
