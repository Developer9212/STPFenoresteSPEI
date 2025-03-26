package fenoreste.spei.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "polizas")
@Data
public class Poliza implements Serializable {

    private Integer idorigenc;
    private String periodo;
    private Integer idtipo;
    @Id
    private Integer idpoliza;



    private static final long serialVersionUID = 1L;
}
