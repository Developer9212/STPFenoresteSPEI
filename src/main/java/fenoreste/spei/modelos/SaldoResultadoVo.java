package fenoreste.spei.modelos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class SaldoResultadoVo {
      
	private Integer estado;
	private String mensaje;
	private SaldoVo respuesta;
	
	@JsonInclude(value = Include.NON_NULL)
	  private Integer codigo;
}
