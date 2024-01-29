package fenoreste.spei.stp;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
public class MethodTest {


    public static void main(String[] args) {
        // URL del servicio REST
        String url = "https://demo.stpmex.com:7024/speiws/rest/ordenPago/registra";

        // Cuerpo de la solicitud (puedes ajustar esto según tus necesidades)
        String requestBody = "{\"claveRastreo\":\"QZjM7Qxr\",\"conceptoPago\":\"\",\"cuentaBeneficiario\":\"\",\"empresa\":\"\",\"institucionContraparte\":\"90646\",\"institucionOperante\":\"\",\"monto\":\"10.0\",\"nombreBeneficiario\":\"\",\"nombreOrdenante\":\"SSTRENLLE VWZLASZ MWLDENWDE\",\"referenciaNumerica\":\"zV0ejxKoeW\",\"rfcCurpBeneficiario\":\"\",\"rfcCurpOrdenante\":\"VWMS759595ECLZLT95\",\"tipoCuentaBeneficiario\":\"40\",\"tipoCuentaOrdenante\":\"40\",\"tipoPago\":\"1\",\"firma\":\"i2U8HDrze8wZpXYfTXEhPXOtNeXPf883bJP+O7aG7tRWmDPqJf35u4PMsvgL7b6/l6wxsqgQDwNJKRmmMqu9no0VIn7Cla2GMOsfJ+7sHWx7NSJfTg4LTAssYEmnUZHtWzocix5ublbamGn6SivpyxNRVlx8yPXCEjZX6jK6IeNxCid5m3IU44Q6Bohus0Q3BZGx1kgBUWTikuKA+4n/HzS+/N+Bf5y2ttBkigpLpctW4ZC0WmXLovOe33Eh3A9rTZp2BaDC+Zzkn9dEYTXZvlhrTyf5Nr2Xxt6qJ7icwyYxV7Z3OoLm9wsLcPNNreC22JNek3MciwUmzWp2qlVI3g\\u003d\\u003d\"}";

        // Configurar encabezados de la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear una entidad HTTP con el cuerpo y los encabezados
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Crear un objeto RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Realizar la solicitud PUT
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        // Verificar si la solicitud fue exitosa (código de respuesta 2xx)
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            // Imprimir la respuesta del servidor
            System.out.println("Respuesta del servidor: " + responseEntity.getBody());
        } else {
            // Imprimir el código de respuesta en caso de error
            System.err.println("Error en la solicitud. Código de respuesta: " + responseEntity.getStatusCodeValue());
        }
    }
}
