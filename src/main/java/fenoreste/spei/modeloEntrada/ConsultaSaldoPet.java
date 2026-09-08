package fenoreste.spei.modelos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ConsultaSaldoPet {
	private String cuentaOrdenante;
	private String empresa;
	private String firma;
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	private Integer fecha;
}
