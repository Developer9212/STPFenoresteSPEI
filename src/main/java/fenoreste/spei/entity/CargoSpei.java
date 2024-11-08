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

    @Id
    @GeneratedValue(generator="sec_spei_enviado")
    @SequenceGenerator(name="sec_spei_enviado",sequenceName="sec_spei_enviado", allocationSize=1)
    private Integer id;
    private String institucioncontraparte;
    private String empresa;
    private String claverastreo;
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
    private Integer referencianumerica;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaentrada;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaejecucion;
    private Integer idorden;
    private boolean aplicado;
    private String estatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_actualizacion_estado;
    private String mensaje_core;

    private static final long serialVersionUID = 1L;

}
