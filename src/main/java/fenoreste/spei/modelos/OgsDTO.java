package fenoreste.spei.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OgsDTO implements Serializable {
    private int idorigen;
    private int idgrupo;
    private int idsocio;

    private static final long serialVersionUID = 1L;
}
