package fenoreste.spei.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fenoreste.spei.modelos.ConcResultadoVo;
import fenoreste.spei.modelos.ConsultaSaldoPet;
import fenoreste.spei.modelos.SaldoResultadoVo;
import fenoreste.spei.service.InServiceGeneral;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping({ "/conciliacion" })
public class ConciliacionController {

   
	@Autowired
	private InServiceGeneral inServiceGeneral;
	
	@GetMapping()
	public ResponseEntity<?> conciliacion(@RequestParam(name = "page",defaultValue = "0")int page,
										  @RequestParam(name="tipoorden",defaultValue ="E")String tipoOrden) {
		
		ConcResultadoVo conciliacion = inServiceGeneral.conciliacion(page,tipoOrden,null);
		if(conciliacion.getCodigo() == 200) {
		   return ResponseEntity.status(HttpStatus.ACCEPTED).body(conciliacion);
		}else {			
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(conciliacion);
	    } 
	}
		
	@GetMapping(value="/historico")
	public ResponseEntity<?> conciliacionHist(@RequestParam(name = "page",defaultValue = "0")int page,
											  @RequestParam(name="tipoorden",defaultValue ="E")String tipoOrden,
											  @RequestParam(name="fecha")Integer fecha) {
			
	ConcResultadoVo conciliacion = inServiceGeneral.conciliacion(page,tipoOrden,fecha);
		if(conciliacion.getCodigo() == 200) {
		   return ResponseEntity.status(HttpStatus.ACCEPTED).body(conciliacion);
		}else {			
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(conciliacion);
	  }
		
	}
	
	
	@GetMapping(value="/consultaSaldo")
	public ResponseEntity<?> consultaSaldon(@RequestParam(name = "clabe",defaultValue = "0")String clabe,
										  @RequestParam(name="fecha",defaultValue ="")String fecha) {
		
		SaldoResultadoVo consulta = inServiceGeneral.consultaSaldo(clabe,fecha);
		if(consulta.getCodigo() == 200) {
		   return ResponseEntity.status(HttpStatus.ACCEPTED).body(consulta);
		}else {			
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(consulta);
	    } 
	}
}
