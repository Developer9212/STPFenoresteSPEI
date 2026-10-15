package fenoreste.spei.modeloSalida;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EstadoRequestVo implements Serializable{

	 private Integer id;
	 private String empresa;
	 private String folioOrigen;
	 private String claveRastreo;
	 private String estado;//LIQUIDADO, CANCELADO, DEVUELTO
	 private String causaDevolucion;
	 private String tsLiquidacion;

	 private static final long serialVersionUID = 1L;
}
