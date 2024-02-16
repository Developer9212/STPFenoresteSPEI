package fenoreste.spei.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fenoreste.spei.modelos.request;
import fenoreste.spei.modelos.response;
import fenoreste.spei.service.InServiceGeneral;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping({ "/api/sendAbono" })
@Slf4j
public class InController {
	
	@Autowired
	private InServiceGeneral serviceGeneral;

	@PostMapping
	public ResponseEntity<?>sendAbono(@RequestBody request inData){
		response responseSendAbono = new response();
		try {
			responseSendAbono = serviceGeneral.response(inData);
			if(responseSendAbono.getCodigo() == 200) {
				return ResponseEntity.status(200).body(responseSendAbono);	
			}else {
				return ResponseEntity.status(400).body(responseSendAbono);
			}
				
		 } catch (Exception e) {
			log.info("Error al abonar:"+e.getMessage());
			return ResponseEntity.status(500).body(e.getMessage());
		}
		
	}
	
}
