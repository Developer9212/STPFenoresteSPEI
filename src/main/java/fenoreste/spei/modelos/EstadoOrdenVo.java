package fenoreste.spei.modelos;

import lombok.Data;

import java.io.Serializable;

@Data
public class EstadoOrdenVo implements Serializable {

    private Integer id;
    private String empresa;
    private String folioOrigen;
    private String estado;
    private String causaDevolucion;

    private static final long serialVersionUID = 1L;

    //{"id":1304170159,"empresa":"CSN795","folioOrigen":"CSN79520240919234958963","estado":"Liquidacion","causaDevolucion":""}
}
