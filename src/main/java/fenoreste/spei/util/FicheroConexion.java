package fenoreste.spei.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.springframework.stereotype.Service;

@Service
public class FicheroConexion {
   
	String fichero = "spei.txt";
    String nbd = "";
    String ipbd = "";

    public FicheroConexion(){
        leeTxt();
    }

    private String obtenerDireccionTxt() {
        return System.getProperty("user.home");
    }

    private String obeterSeparador() {
        return System.getProperty("file.separator");
    }

    private File obtenerTxt() {
        String ruta_fichero = obtenerDireccionTxt() + obeterSeparador() + fichero;
        File f = new File(ruta_fichero);
        if (f.exists()) {
            return f;
        } else {
            System.out.println("El fichero no existe: " + ruta_fichero);
            return null;
        }
    }

    private void leeTxt() {
        if (obtenerTxt() != null) {
         try {
            FileReader fr = new FileReader(obtenerTxt());
            BufferedReader br = new BufferedReader(fr);
            String linea;
            while ((linea = br.readLine()) != null) {
                     leer_lineas(linea);
            }
            } catch (Exception e) {
                System.out.println("Excepcion leyendo el txt " + fichero + ": " + e);
            }
        } else {
            System.out.println("No se encontro el fichero.");
        }
    }

    private void leer_lineas(String linea) {
        if (linea.contains("base_de_datos")) {
            nbd = linea.split("=")[1].trim();
        }
        if (linea.contains("direccion_servidor")) {
            ipbd = linea.split("=")[1].trim();
        }
    }

    public String getDatabase() {
        return nbd;
    }

    public String getHost() {
        return ipbd;
    }
}
