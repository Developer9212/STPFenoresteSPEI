package fenoreste.spei.consumo;

import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SMSCsn {

    public String enviarSMS(String host, String numero, String mensaje) {
        String r = "";
        System.out.println("Enviando SMS al host"+host+", Numero:"+numero+",mensaje:"+mensaje);

        if (host != null && numero != null && mensaje != null) {
            numero = numero.replace(" ", "");
            numero = numero.trim();
            mensaje = mensaje.replace(" ", "%20");
            mensaje = mensaje.trim();
            if (numero.length() == 10) {
                if (mensaje.length() <= 160) {
                    //WsConnExternos wsConnExternos = new WsConnExternos();//esta es la clase en donde se genera una conexion y se lee lo que retorna
                    host = host.replace("_mensaje", mensaje);
                    host = host.replace("_numero", numero);
                    r = simpleConexionURL(host);
                } else {
                    r = "Error: Mensaje mayor a 160 caracteres en EnvioSMS";
                    System.out.println("Error: Mensaje mayor a 160 caracteres en EnvioSMS" + r);//mensaje mayor a 160 caracteres
                }
            } else {
                r = "Error: Numero invalido en EnvioSMS.";
                System.out.println("Error: Numero invalido en EnvioSMS. " + r); //numero invalido
            }
        } else {
            r = "URL para envio SMS invalida";
            System.out.println("url: " + host + "\nnumero: " + numero + "\nmensaje: " + mensaje);
        }
        return r;
    }

    public String simpleConexionURL(String urlString) {
        String mensaje_ok_envio = "";
        try {
            // Creo un objeto url con la cadena
            URL url = new URL(urlString);
            System.out.println("URL:"+url);
            // Creo un objeto HttpURLConnection para conectarme
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // El tiempo de espera para que se conecte
            con.setConnectTimeout(6000);
            // Este es el tiempo de espera de la lectura de lo que retorno en caso de que retorne
            con.setReadTimeout(3000);
            //Si la conexion fue exitosa
            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("No se envio el mensaje, error en conexion.");
                mensaje_ok_envio="Error desconocido no se pudo enviar el SMS";
            }else{
                mensaje_ok_envio="SMS enviado con exito";
            }
        } catch (Exception e) {
            mensaje_ok_envio = "Error desconocido no se pudo enviar el SMS:"+e.getMessage();
            return mensaje_ok_envio;// "Exception en WsConnExternos.simpleConeccionURL " + e.getMessage();
        }
        return mensaje_ok_envio;
    }
}
