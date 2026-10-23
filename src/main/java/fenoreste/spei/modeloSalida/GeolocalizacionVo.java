package fenoreste.spei.modeloSalida;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GeolocalizacionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ip;
    private String ciudad;
    private String region;
    private String country;
    private String pais;
    private Double latitud;
    private Double longitud;

}
