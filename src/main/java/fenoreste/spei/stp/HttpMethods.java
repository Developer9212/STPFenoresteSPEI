package fenoreste.spei.stp;


import fenoreste.spei.util.SSLUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import fenoreste.spei.entity.Tabla;
import fenoreste.spei.entity.TablaPK;
import fenoreste.spei.service.ITablaService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@Slf4j
public class HttpMethods {

    @Autowired
    private ITablaService tablaService;

    @Autowired
    private SSLUtil sslUtil;

    OkHttpClient client = null;
    MediaType mediaType = null;
    RequestBody body = null;
    Request request = null;
    Response response = null;


    private String path = "";
    private String endpointRegistraOrden = "ordenPago/registra";
    private String endpointConciliacion = "conciliacion/actual";//Se modifica el 21/10/2025 por actualizacion de STP
    private String endpointConciliacionHis = "conciliacion/historica";//Se modifica el 21/10/2025 por actualizacion de STP
    private String endpointConsultaSaldo = "consultaSaldoCuenta";
    private String endpointConsultaOperacionesActual = "operaciones/actual";
    private String endpointConsultaOperacionesHistorica = "operaciones/historica";
    private String endpointConsultaOperacionesFechaN = "operaciones/fechaNatural";

    Gson gson = new Gson();

    private String idtabla = "spei_salida";

    public String enviarOrdenSpei(String requestPeticion) {
        String resultado = "";
        String p = "{\n" +
                "  \"claveRastreo\": \"20260928429640702\",\n" +
                "  \"conceptoPago\": \"Pago\",\n" +
                "  \"cuentaBeneficiario\": \"646180110400000007\",\n" +
                "  \"fechaOperacion\": \"20260929\",\n" +
                "  \"cuentaOrdenante\": \"646180522900000001\",\n" +
                "  \"empresa\": \"CAJA_FAMA\",\n" +
                "  \"firma\": \"GDnf5XvVC9h5ymMGhS93IJu2uQrjxM47tJJ8Lrc5hmbue9cgCZ1nj0/ZMYvb3LxuM1VXgMumArJyIAHcQ6sVNY3Qz9FhU+3nDR01E4y8XmV8DC+z4HJHsNPZwh3LVTXQu8c0xEWrHT6oiiIps23TWgIBPlp6Vw7VWM3NBpy1pVSsoR3b7duPvnMq6W9lpmcVOD3N8PkIsHH+x6/GEjGHjK+qslx81epdGxA1cx3v00cYQk7mEv4XBbJydkwuzJRtUnu7gASypfA3+zkDqnnPHTRIcD+LJ/VbB13rgNh1Gj2hbYxkFqMJj2rhU/3r9mqofSeeC9ob0p55Zs9UHUH1Fg==\",\n" +
                "  \"institucionContraparte\": \"90646\",\n" +
                "  \"institucionOperante\": \"90646\",\n" +
                "  \"latitud\": \"25.647914\",\n" +
                "  \"longitud\": \"-100.290778\",\n" +
                "  \"monto\": \"1000.00\",\n" +
                "  \"nombreBeneficiario\": \"maria antonia munoz ortiz\",\n" +
                "  \"nombreOrdenante\": \"CajaFama\",\n" +
                "  \"referenciaNumerica\": \"138476\",\n" +
                "  \"rfcCurpBeneficiario\": \"LOFJ790906HNLPLN08\",\n" +
                "  \"rfcCurpOrdenante\": \"ND\",\n" +
                "  \"tipoCuentaBeneficiario\": \"40\",\n" +
                "  \"tipoCuentaOrdenante\": \"40\",\n" +
                "  \"tipoPago\": \"1\"\n" +
                "}";

        String p1="{\n" +
                "  \"claveRastreo\": \"202609284296407021\",\n" +
                "  \"conceptoPago\": \"Pago\",\n" +
                "  \"cuentaBeneficiario\": \"646180110400000007\",\n" +
                "  \"fechaOperacion\": \"20260929\",\n" +
                "  \"cuentaOrdenante\": \"646180522900000001\",\n" +
                "  \"empresa\": \"CAJA_FAMA\",\n" +
                "  \"firma\": \"egFvPu2i9ve42cR7++9+wWb64Qmf+fOvZzYFeeKgFTD8ZTrpisC1sIWHj4oIxQdqPg1TksE1bkCMGuvL+xpQoui6l8A/aYcFRyeP3mzr21EwC8zS7orr5g9AnotPUqV3ZQPOvJUkkTCW7FR1yjN4EMghvAsviTJirJY5XEBBl0UVD/DSVdD/DPNpbvwmo4BbRjtl5jH+6nfV5KI0eEhtWEWqPEDnxVpu/yhM1VzUbC2pQS695SYT2EElEyP1CLGS6hSwU7DUPea/bA/Or84xAGM5uSQppYsUFPtIrMpf6LFHuqs1PMmLGrDntRml6DsGCLGRnzczdk7JTm/gQgwPIQ==\",\n" +
                "  \"institucionContraparte\": \"90646\",\n" +
                "  \"institucionOperante\": \"90646\",\n" +
                "  \"latitud\": \"25.647914\",\n" +
                "  \"longitud\": \"-100.290778\",\n" +
                "  \"monto\": \"1000.00\",\n" +
                "  \"nombreBeneficiario\": \"maria antonia munoz ortiz\",\n" +
                "  \"nombreOrdenante\": \"CajaFama\",\n" +
                "  \"referenciaNumerica\": \"138476\",\n" +
                "  \"rfcCurpBeneficiario\": \"LOFJ790906HNLPLN08\",\n" +
                "  \"rfcCurpOrdenante\": \"ND\",\n" +
                "  \"tipoCuentaBeneficiario\": \"40\",\n" +
                "  \"tipoCuentaOrdenante\": \"40\",\n" +
                "  \"tipoPago\": \"1\"\n" +
                "}";
        log.info("Peticion hacia STP:"+requestPeticion);
        log.info("Peticion hacia P:"+p1);
        try {
            JSONObject json = new JSONObject();
            client = new OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS) // Tiempo de espera para establecer la conexión
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)    // Tiempo de espera para la lectura de datos
                    .build();
            mediaType = MediaType.parse("application/json");

            body = RequestBody.create(mediaType, requestPeticion);
            String url = formaUrl(1) + endpointRegistraOrden;
            log.info(url);
            System.out.println("Url a consumir:" + url);
            request = new Request.Builder()
                    .url(url)
                    .method("PUT", body).addHeader("Content-Type", "application/json").build();
            response = client.newCall(request).execute();
            resultado = response.body().string();
            log.info("Resultado STP Enviar Orden:" + resultado);
        } catch (Exception e) {
            log.info("Error al enviar orden spei:" + e.getMessage());
        }

        return resultado;
    }


    public String conciliacion(String requestPeticion) {
        String resultado = "";
        try {
            JSONObject json = new JSONObject();
            client = sslUtil.OKHttpClientModificado();
            log.info("Peticion conciliacion(consulta operaciones actual):" + requestPeticion);
            mediaType = MediaType.parse("application/json");
            body = RequestBody.create(mediaType, requestPeticion);
            String url = formaUrl(2) + endpointConsultaOperacionesActual;
            log.info("::::::::::::::::::Url a consumir conciliacion(operaciones actual)::::::::::::" + url);
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body).addHeader("Content-Type", "application/json").build();
            response = client.newCall(request).execute();
            resultado = response.body().string();
            log.info("::::Resultado STP enpoint conciliacion(consulta operaciones):"+resultado);
        } catch (Exception e) {
            log.info("Error al consumir concilicacion(operaciones actual):" + e.getMessage());
        }
        return resultado;
    }


    public String conciliacionHistorica(String requestPeticion) {
        String resultado = "";
        try {
            JSONObject json = new JSONObject();
            client = sslUtil.OKHttpClientModificado();
            log.info("Peticion conciliacion historica(operaciones historica):" + requestPeticion);
            mediaType = MediaType.parse("application/json");
            body = RequestBody.create(mediaType, requestPeticion);
            String url = formaUrl(2) + endpointConsultaOperacionesHistorica;
            log.info("::::::::::::::::::Url a consumir conciliacion(operaciones historica)::::::::::::" + url);
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body).addHeader("Content-Type", "application/json").build();
            response = client.newCall(request).execute();
            resultado = response.body().string();
            log.info("::::Resultado STP enpoint conciliacion(operaciones historica):"+resultado);
        } catch (Exception e) {
            log.info("Error al consumir concilicacion(operaciones historica):" + e.getMessage());
        }
        return resultado;
    }



    public String consultaSaldo(String requestPeticion) {
        String resultado = "";
        try {
            JSONObject json = new JSONObject();
            client = sslUtil.OKHttpClientModificado();
            log.info("Peticion consulta saldo:" + requestPeticion);
            mediaType = MediaType.parse("application/json");
            body = RequestBody.create(mediaType, requestPeticion);
            String url = formaUrl(3) + endpointConsultaSaldo;
            log.info("Consultando la URL:" + url);
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body).addHeader("Content-Type", "application/json").build();
            response = client.newCall(request).execute();
            resultado = response.body().string();
            log.info("Resultado STP:" + resultado);
        } catch (Exception e) {
            log.info("Error al consultar saldo:" + e.getMessage());
        }
        return resultado;
    }

    public String formaUrl(Integer opcion) {//1.-Spei salida 2.-Conciliacion
        String url = "";
        try {
            TablaPK PkUrl = null;
            Tabla tablaUrl = null;
            switch (opcion) {
                case 1:
                    PkUrl = new TablaPK(idtabla, "stppath");
                    tablaUrl = tablaService.buscarPorId(PkUrl);
                    url = tablaUrl.getDato2().trim() + "/";
                    break;
                case 2:
                    PkUrl = new TablaPK("conciliacion", "stppath");
                    tablaUrl = tablaService.buscarPorId(PkUrl);
                    url = tablaUrl.getDato2() + tablaUrl.getDato1() + "/";
                    break;
                case 3:
                    PkUrl = new TablaPK("conciliacion", "consulta_saldo_stppath");
                    tablaUrl = tablaService.buscarPorId(PkUrl);
                    url = tablaUrl.getDato2() + tablaUrl.getDato1() + "/";
                    break;
            }

        } catch (Exception e) {
            log.info("Error al formar url:" + e.getMessage());
        }
        return url;
    }


}
