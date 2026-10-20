package fenoreste.spei.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpeiEnviadoPK implements Serializable {

    private static final long SerialVersionUID = 1L;
    private  Integer idorden;
    private Integer referenciaNumerica;
    private String claveRastreo;
}
