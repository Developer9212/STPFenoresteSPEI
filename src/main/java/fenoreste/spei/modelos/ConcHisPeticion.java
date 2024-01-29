package fenoreste.spei.modelos;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConcHisPeticion implements Serializable{

	 private String empresa;
	 private Integer fechaOperacion;
	 private String firma;
	 private Integer page;
	 private String tipoOrden;
		 
	private static final long serialVersionUID = 1L;
}
