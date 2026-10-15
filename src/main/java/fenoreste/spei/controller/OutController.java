package fenoreste.spei.controller;

import fenoreste.spei.modeloEntrada.ResponseLocalDispersionVo;
import fenoreste.spei.modeloSalida.EstadoRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fenoreste.spei.modeloEntrada.RequestLocalDispersionVo;
import fenoreste.spei.service.OutServiceGeneral;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping({ "/api/dispersion" })
@Slf4j
public class OutController {
	
	@Autowired
	private OutServiceGeneral outServiceGeneral;	

	@PostMapping(value = "/sendOrder",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?>sendOrder(@RequestBody RequestLocalDispersionVo inData){
		log.info("::::::::::::::Vamos a enviar una orden SPEI::::::::::::::::::::::"+inData);
		ResponseLocalDispersionVo response = outServiceGeneral.sendOrder(inData);
		return ResponseEntity.status(200).body(response);
	}

	//Solo caja fama
	@GetMapping(value = "/sendOrderFama")//, consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?>sendOrderFama(@RequestBody RequestLocalDispersionVo inData){
		log.info("::::::::::::::Vamos a enviar una orden SPEI(Fama 1)::::::::::::::::::::::"+inData);
		ResponseLocalDispersionVo response = outServiceGeneral.sendOrder(inData);
		return ResponseEntity.status(200).body(response);
	}


	@PostMapping(value = "/cambio/estado",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?>actualizaEstado(@RequestBody EstadoRequestVo estadoRequestVo) {
		log.info("::::::::::::::Cambio estado orden:::::::::::::::::::::::"+estadoRequestVo);
		//ResponseLocalDispersionVo response = outServiceGeneral.sendOrder(inData);
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println("siiiiii");
			jsonObject = outServiceGeneral.cambioEstado(estadoRequestVo);
		}catch (Exception e) {
		   log.info("::::Error al manejar Json:"+e.getMessage());
		}
		return ResponseEntity.status(200).body(jsonObject.toString());
	}

	/*
	@PostMapping(value = "/actualizarEstado",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?>actualizaEstado(@RequestBody EstadoOrdenVo estatus){
		log.info(":::::::::::::::Vamos a actualizar el estado de una orden:"+estatus.getId()+"::::::::::::::");
		return ResponseEntity.status(200).body(outServiceGeneral.sendOrder(estatus));
		
	}*/
	
	
}
