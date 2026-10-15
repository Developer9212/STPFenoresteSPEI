package fenoreste.spei.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="estadosordenspei")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EstadoSpei implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;
    private String empresa;
    private String folioorigen;
    private String claverastreo;
    private String estado;//LIQUIDADO, CANCELADO, DEVUELTO
    private String causadevolucion;
    private String tsliquidacion;
    @Temporal(TemporalType.DATE)
    private Date fechaactualizacion;

}
