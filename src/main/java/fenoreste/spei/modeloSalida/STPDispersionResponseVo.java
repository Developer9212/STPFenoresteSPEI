package fenoreste.spei.modelos;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(value = Include.NON_EMPTY)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class STPDispersionResponseVo implements Serializable{
    
	private STPResultadoVo resultado;
	
	private static final long serialVersionUID = 1L;
}
