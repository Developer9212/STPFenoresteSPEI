package fenoreste.spei.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLocalDispersionVo implements Serializable {

    int id;
    String error;

    private static final long serialVersionUID = 1L;
}
