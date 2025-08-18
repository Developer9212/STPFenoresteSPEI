package fenoreste.spei.controller;

import fenoreste.spei.modelos.request;
import fenoreste.spei.modelos.response;
import fenoreste.spei.service.IFuncionesSaiService;
import fenoreste.spei.service.InServiceGeneral;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/sendAbono")
@Slf4j
public class InController {

	@Autowired
	private InServiceGeneral serviceGeneral;

	@Autowired
	private IFuncionesSaiService saiService;

	// Endpoint de prueba para verificar conexión al servidor
	@GetMapping
	public ResponseEntity<Date> test() {
		Date date = saiService.dateServidorBase();
		log.info("Prueba de conexión, fecha desde BD: {}", date);
		return ResponseEntity.ok(date);
	}

	// Endpoint para enviar abono
	@PostMapping
	public ResponseEntity<response> sendAbono(@RequestBody request inData) {
		response responseSendAbono = new response();
		try {
			log.info("Solicitud recibida: {}", inData);

			responseSendAbono = serviceGeneral.sendAbono(inData);

			if (responseSendAbono == null) {
				log.warn("Respuesta nula del servicio.");
				responseSendAbono = new response();
				responseSendAbono.setId(21);
				responseSendAbono.setMensaje("devolver");
				return ResponseEntity.status(500).body(responseSendAbono);
			}

			if (responseSendAbono.getCodigo() == 200) {
				log.info("Abono exitoso: {}", responseSendAbono);
				return ResponseEntity.ok(responseSendAbono);
			} else {
				log.warn("Abono con error controlado: {}", responseSendAbono);
				return ResponseEntity.status(400).body(responseSendAbono);
			}

		} catch (Exception e) {
			log.error("Error inesperado al procesar abono", e);
			responseSendAbono = new response();
			responseSendAbono.setId(21);
			responseSendAbono.setMensaje("devolver");
			return ResponseEntity.status(500).body(responseSendAbono);
		}
	}
}
