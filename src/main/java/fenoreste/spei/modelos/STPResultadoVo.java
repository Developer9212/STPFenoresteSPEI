package fenoreste.spei.modelos;

import java.io.Serializable;

import lombok.Data;

@Data
public class STPResultadoVo implements Serializable{

    private String descripcionError;
    private Integer id;

    private static final long serialVersionUID = 1L;

}
