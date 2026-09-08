package fenoreste.spei.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="speienviado")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CargoSpei implements Serializable {

    @EmbeddedId
    private SpeiEnviadoPK speiEnviadoPK;
    private String institucioncontraparte;
    private String empresa;
    private Integer institucionoperante;
    private double monto;
    private Integer tipopago;
    private Integer tipocuentaordenante;
    private String nombreordenante;
    private String cuentaordenante;
    private String rfccurpordenante;
    private Integer tipocuentabeneficiario ;
    private String nombrebeneficiario;
    private String cuentabeneficiario;
    private String rfccurpbeneficiario;
    private String conceptopago;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaejecucion;
    private boolean aplicado;
    private String estatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_actualizacion_estado;
    private String mensaje_core;

    private static final long serialVersionUID = 1L;

}
