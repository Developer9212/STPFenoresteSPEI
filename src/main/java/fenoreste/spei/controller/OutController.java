package fenoreste.spei.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fenoreste.spei.modelos.RequestLocalDispersionVo;
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

		return ResponseEntity.status(200).body(outServiceGeneral.sendOrder(inData));
		
	}
	
	/*@PostMapping(value = "/changeStatus",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?>actualizaEstado(@RequestBody EstadoVo estatus){
		return ResponseEntity.status(200).body(outServiceGeneral.sendOrder(inData));
		
	}*/
	
	
}
