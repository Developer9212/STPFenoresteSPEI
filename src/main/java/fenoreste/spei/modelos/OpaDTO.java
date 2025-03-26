package fenoreste.spei.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpaDTO implements Serializable {
    private int idorigenp;
    private int idproducto;
    private int idauxiliar;

    private static final long serialVersionUID = 1L;

}
