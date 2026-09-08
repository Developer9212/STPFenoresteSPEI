package fenoreste.spei.modelos;

import java.io.Serializable;

import lombok.Data;

@Data
public class EstadoRequestVo implements Serializable{

	 private Integer id;
	 private String empresa;
	 private String folioOrigen;
	 private String estado;//LIQUIDADO, CANCELADO, DEVUELTO
	 private String causaDevolucion;

	 private static final long serialVersionUID = 1L;
}
