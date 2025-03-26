package fenoreste.spei.util;

import fenoreste.spei.modelos.OpaDTO;
import fenoreste.spei.modelos.OgsDTO;
import org.springframework.stereotype.Service;

@Service
public class Util {

    public OpaDTO opa(String cadena){
        OpaDTO opa=new OpaDTO();
        try {
            System.out.println("sooooooooooooooooooooooo:"+cadena);
            opa.setIdorigenp(Integer.parseInt(cadena.substring(0, 6)));
            System.out.println(cadena.substring(0, 6));
            opa.setIdproducto(Integer.parseInt(cadena.substring(6, 11)));
            opa.setIdauxiliar(Integer.parseInt(cadena.substring(11, 19)));
        } catch (Exception e) {
            System.out.println("Error al deserealizar opa:"+e.getMessage());
        }
        return opa;
    }

    public OgsDTO ogs(String cadena){
        OgsDTO ogs=new OgsDTO();
        try {
            ogs.setIdorigen(Integer.parseInt(cadena.substring(0, 6)));
            ogs.setIdgrupo(Integer.parseInt(cadena.substring(6, 8)));
            ogs.setIdsocio(Integer.parseInt(cadena.substring(8, 14)));
        } catch (Exception e) {
            System.out.println("Error al deserealizar ogs:"+e.getMessage());
        }
        return ogs;
    }
}
