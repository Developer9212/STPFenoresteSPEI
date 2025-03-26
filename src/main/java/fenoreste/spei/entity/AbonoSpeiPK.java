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
public class AbonoSpeiPK implements Serializable {

    private Integer id;
    private String claverastreo;

    private static final long serialVersionUID = 1L;
}
