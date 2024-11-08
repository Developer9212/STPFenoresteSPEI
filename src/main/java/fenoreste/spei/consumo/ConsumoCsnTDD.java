package fenoreste.spei.consumo;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ConsumoCsnTDD{
	
	
	@Autowired	
	private static RestTemplate restTemplate=new RestTemplate();
	
	private static String basePath = "/api/cards/";
	
	public String obtenerSaldo(String url,String idtarjeta) {
		JSONObject json = new JSONObject();
		ResponseEntity<String> requisition = null;
		try {			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>("", headers);
			requisition =  restTemplate.exchange(url+basePath + "/getBalanceQuery/idcard="+idtarjeta, HttpMethod.GET, entity,String.class);
			json = new JSONObject(requisition.getBody());

			//json.put("availableAmount","20000.00");
		} catch (JSONException e) {
			System.out.println("Error al obtener saldo de cuenta:"+idtarjeta+","+ e.getMessage());
		}

		return  json.toString();
	}
	
	public boolean retirarSaldo(String url,String idtarjeta,Double monto) {
		boolean bandera= false;
		/*ResponseEntity<String> requisition = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>("", headers);
			requisition =  restTemplate.exchange(url+basePath + "doWithdrawal/idcard="+idtarjeta+"&amount="+monto, HttpMethod.GET, entity,String.class);
			if(requisition.getStatusCode() == HttpStatus.OK) {
				bandera = true;
			}else {
				bandera = false;
			}
		} catch (Exception e) {
			System.out.println("Error al realizar retiro de tarjeta:"+idtarjeta+"," + e.getMessage());
		}*/
		return true;//bandera;
	}
	
	public boolean depositarSaldo(String url,String idtarjeta,Double monto) {
		ResponseEntity<String> requisition = null;
		/*boolean bandera = false;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<String>("", headers);
			requisition =  restTemplate.exchange(url+basePath + "loadBalance/idcard="+idtarjeta+"&monto="+monto, HttpMethod.GET, entity,String.class);
		    if(requisition.getStatusCode() == HttpStatus.OK) {
		    	bandera =  true;
		    }else {
		    	System.out.println("Aqui se dio error");
		    }
		} catch (Exception e) {
			System.out.println("Error al deposito de tarjeta:"+idtarjeta+"," + e.getMessage());
			
		}*/

		return true;//bandera;
	}
	
      
	
}
