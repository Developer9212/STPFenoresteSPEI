package fenoreste.spei.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fenoreste.spei.modelos.EstadoVo;
import fenoreste.spei.modelos.OrdenVo;
import fenoreste.spei.service.OutServiceGeneral;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping({ "dispersion" })
public class OutController {
	
	@Autowired
	private OutServiceGeneral outServiceGeneral;	

	@PostMapping(value = "/sendOrder",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?>sendOrder(@RequestBody OrdenVo inData){
		return ResponseEntity.status(200).body(outServiceGeneral.sendOrder(inData));
		
	}
	
	/*@PostMapping(value = "/changeStatus",consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?>actualizaEstado(@RequestBody EstadoVo estatus){
		return ResponseEntity.status(200).body(outServiceGeneral.sendOrder(inData));
		
	}*/
	
	
}
