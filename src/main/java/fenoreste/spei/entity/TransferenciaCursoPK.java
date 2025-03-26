package fenoreste.spei.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TransferenciaCursoPK implements java.io.Serializable {

    private Integer id;
    private String clabe;
    private String claverastreo;
    private String cuentaordenante;
    private Integer cargoabono;

}
