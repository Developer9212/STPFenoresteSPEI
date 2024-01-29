package fenoreste.spei.modelos;

import java.io.Serializable;

import lombok.Data;

@Data
public class DispersionVo implements Serializable{
	
	private Integer codigo;
	private Integer idOrden;
	private String mensaje;
	private String fechaOperacion;
	
	private static final long serialVersionUID = 1L;
}
