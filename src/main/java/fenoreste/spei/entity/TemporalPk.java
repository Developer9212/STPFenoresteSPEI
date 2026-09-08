package fenoreste.spei.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemporalPk implements Serializable {
    private Integer idusuario;
    private String sesion;
    private String referencia;

    private static final long serialVersionUID = 1L;
}
