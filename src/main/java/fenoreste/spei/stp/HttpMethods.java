package fenoreste.spei.stp;


import fenoreste.spei.modelos.STPResultadoVo;
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
	private String endpointConciliacion = "conciliacion";
	private String endpointConsultaSaldo = "consultaSaldoCuenta";
	
	Gson gson = new Gson();
	
	private String idtabla = "spei_salida";
	
	public String enviarOrdenSpei(String requestPeticion) {
		 String resultado = "";
		 try {
			 JSONObject json = new JSONObject();
				client = new OkHttpClient.Builder()
		                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS) // Tiempo de espera para establecer la conexión
		                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)    // Tiempo de espera para la lectura de datos
		                .build();
				mediaType = MediaType.parse("application/json");
				
				body = RequestBody.create(mediaType,requestPeticion);
				String url = formaUrl(1)+endpointRegistraOrden;
				System.out.println("Url a consumir:"+url);
				request = new Request.Builder()
						.url(url)
						.method("PUT", body).addHeader("Content-Type", "application/json").build();
				response = client.newCall(request).execute();
				resultado = response.body().string();
				log.info("Resultado STP:"+resultado);
		} catch (Exception e) {
			log.info("Error al enviar orden spei:"+e.getMessage());
		}

		 return resultado;
	 }
	
	
	public String conciliacion(String requestPeticion) {
		 String resultado = "";
		 try {
			 JSONObject json = new JSONObject();

			 sslUtil.disableSSLCertificateChecking();

				client = new OkHttpClient.Builder()
		                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS) // Tiempo de espera para establecer la conexión
		                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)    // Tiempo de espera para la lectura de datos
		                .build();
				log.info("Peticion conciliacion:"+requestPeticion);
				mediaType = MediaType.parse("application/json");
				body = RequestBody.create(mediaType,requestPeticion);
				String url = formaUrl(2)+endpointConciliacion;
				request = new Request.Builder()
						.url(url)
						.method("POST", body).addHeader("Content-Type", "application/json").build();
				response = client.newCall(request).execute();
				resultado = response.body().string();			
				//log.info("Resultado STP:"+resultado);
		} catch (Exception e) {
			log.info("Error al consumir concilicacion:"+e.getMessage());
		}
		 return resultado;
	 }
	
	public String consultaSaldo(String requestPeticion) {
		 String resultado = "";
		 try {
			 JSONObject json = new JSONObject();
				client = new OkHttpClient.Builder()
		                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS) // Tiempo de espera para establecer la conexión
		                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)    // Tiempo de espera para la lectura de datos
		                .build();
				log.info("Peticion consulta saldo:"+requestPeticion);
				mediaType = MediaType.parse("application/json");
				body = RequestBody.create(mediaType,requestPeticion);
				String url = formaUrl(3)+endpointConsultaSaldo;
				log.info("Consultando la URL:"+url);
				request = new Request.Builder()
						.url(url)
						.method("POST", body).addHeader("Content-Type", "application/json").build();
				response = client.newCall(request).execute();
				resultado = response.body().string();			
				log.info("Resultado STP:"+resultado);
		} catch (Exception e) {
			log.info("Error al consultar saldo:"+e.getMessage());
		}
		 return resultado;
	 } 
	 
	public String formaUrl(Integer opcion) {//1.-Spei salida 2.-Conciliacion
		 String url  = "";
		try {
			TablaPK PkUrl = null;
			Tabla tablaUrl = null;
			switch (opcion) {
			case 1:
				PkUrl = new TablaPK(idtabla, "stppath");
				tablaUrl = tablaService.buscarPorId(PkUrl);
				url = tablaUrl.getDato2().trim()+"/";
				break;
			case 2:
				PkUrl = new TablaPK("conciliacion", "stppath");
				tablaUrl = tablaService.buscarPorId(PkUrl);
				url = tablaUrl.getDato2()+tablaUrl.getDato1()+"/";
				break;				
			case 3:
				PkUrl = new TablaPK("conciliacion", "consulta_saldo_stppath");
				tablaUrl = tablaService.buscarPorId(PkUrl);
				url = tablaUrl.getDato2()+tablaUrl.getDato1()+"/";
				break;
			}
			log.info("Url formada:"+url);
		} catch (Exception e) {
			log.info("Error al formar url:"+e.getMessage());
		}
		return url;
	 }
	
	
	
}
