package fenoreste.spei.modelos;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(value = Include.NON_EMPTY)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class response implements Serializable{

	private String mensaje;
	private Integer id;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private Integer codigo;
	
	private static final long serialVersionUID = 1L;
}
