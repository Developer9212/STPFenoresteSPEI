package fenoreste.spei.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import fenoreste.spei.consumo.ConsumoCsnTDD;
import fenoreste.spei.entity.AbonoSpei;
import fenoreste.spei.entity.Auxiliar;
import fenoreste.spei.entity.AuxiliarPK;
import fenoreste.spei.entity.ClabeInterbancaria;
import fenoreste.spei.entity.FolioTarjeta;
import fenoreste.spei.entity.Origen;
import fenoreste.spei.entity.Persona;
import fenoreste.spei.entity.PersonaPK;
import fenoreste.spei.entity.Producto;
import fenoreste.spei.entity.Sopar;
import fenoreste.spei.entity.SpeiTemporal;
import fenoreste.spei.entity.Tabla;
import fenoreste.spei.entity.TablaPK;
import fenoreste.spei.entity.Tarjeta;
import fenoreste.spei.entity.Usuario;
import fenoreste.spei.modelos.ConcPeticionVo;
import fenoreste.spei.modelos.ConcResultadoVo;
import fenoreste.spei.modelos.ConsultaSaldoPet;
import fenoreste.spei.modelos.SaldoResultadoVo;
import fenoreste.spei.modelos.request;
import fenoreste.spei.modelos.response;
import fenoreste.spei.stp.HttpMethods;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InServiceGeneral {
    
	@Autowired
	private IFuncionesSaiService funcionesSaiService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private ITablaService tablasService;
	
	@Autowired
	private IOrigenService origenesService;
	
	@Autowired
	private IClabeInterbancariaService clabeInterbancariaService;
	
	@Autowired 
	private IAbonoSpeiService abonoSpeiService;
	
	@Autowired
	private IFolioTarjetaService folioTarjetaService;
	
	//Commit
	
	@Autowired
	private ITarjetaService tarjetaService;
	
	@Autowired
	private IAuxiliarService auxiliarService;
	
	@Autowired
	private ISpeiTemporalService speiTemporalService;
	
	@Autowired
	private IProductoService productoService;
	
	@Autowired
	private IPersonaService personaService;
	
	@Autowired
	private ISoparService soparService;
	
	@Autowired
	private ConsumoCsnTDD consumoCsnTDD;
	
	@Autowired
	private HttpMethods httpMethods;
	
	Gson json = new Gson();

	
	String idtabla="spei_entrada";
	
	public response response(request in) {
	    response resp = new response();	    
	    resp.setMensaje("devolver");
	    log.info(".......Peticion sendAbono:"+in);
	    resp.setCodigo(400);
	    if(abonoSpeiService.buscarPorId(in.getId()) == null) {	 
	    	log.info("1.-1");
	    //Vamos a registrar la operacion 
	    AbonoSpei operacion = new AbonoSpei();	   
	    operacion.setId(in.getId());
	    operacion.setFechaOperacion(in.getFechaOperacion());
	    operacion.setInstitucionOrdenante(in.getInstitucionOrdenante());
	    operacion.setInstitucionBeneficiaria(in.getInstitucionBeneficiaria());
	    operacion.setClaveRastreo(in.getClaveRastreo());
	    operacion.setMonto(in.getMonto());
	    operacion.setNombreOrdenante(in.getNombreOrdenante());
	    operacion.setTipocuentaOrdenante(in.getTipoCuentaOrdenante());
	    operacion.setCuentaOrdenante(in.getCuentaOrdenante());
	    operacion.setRfccurpOrdenante(in.getRfcCurpOrdenante());
	    operacion.setNombreBeneficiario(in.getNombreBeneficiario());
	    operacion.setTipocuentaBeneficiario(in.getTipoCuentaBeneficiario());
	    operacion.setCuentaBeneficiario(in.getCuentaBeneficiario());
	    operacion.setRfcCurpBeneficiario(in.getRfcCurpBeneficiario());
	    operacion.setConceptoPago(in.getConceptoPago());
	    operacion.setReferenciaNumerica(in.getReferenciaNumerica());
	    operacion.setEmpresa(in.getEmpresa());
	    operacion.setFechaentrada(new Date());
	    operacion.setResponsecode(57);
	    operacion.setAplicado(false);
	    abonoSpeiService.guardar(operacion);
	    log.info("Se guardo registro de la operacion");
	    //validamos el horario de actividad
	    if(funcionesSaiService.horario_actividad()) {
	    	//Obtenemos origen Matriz
	    	Origen matriz = origenesService.buscarMatriz();
	    	//Buscamos el usuario para operar abonos
	    	TablaPK tb_pk= new TablaPK(idtabla,"usuario");
	    	Tabla tb_usuario = tablasService.buscarPorId(tb_pk);
	    	Usuario user_in = usuarioService.buscar(Integer.parseInt(tb_usuario.getDato1()));
	    	//Obtenemos el estatus de origen al que pertenece el usuario
	    	Origen origen_usuario = origenesService.buscarPorId(new Integer(user_in.getIdorigen()));
	    	if(origen_usuario.isEstatus()) {
	    		response valiResponse = new response();
	    		String validacion = "";	    		
	    				switch (in.getTipoCuentaBeneficiario()) {
						case 40:
							ClabeInterbancaria clabe_registro = clabeInterbancariaService.buscarPorClabe(in.getCuentaBeneficiario());
							if(clabe_registro != null) {
								AuxiliarPK a_pk = new AuxiliarPK(clabe_registro.getAuxPk().getIdorigenp(),clabe_registro.getAuxPk().getIdproducto(),clabe_registro.getAuxPk().getIdauxiliar());
								Auxiliar a = auxiliarService.buscarPorId(a_pk);
								if(matriz.getIdorigen() == 30200){//CSN
									valiResponse = validaReglasCsn(a.getAuxiliarPK(),in.getMonto(),in.getFechaOperacion(), 0);
					    		}else if(matriz.getIdorigen() == 30300) {//Mitras
					    			valiResponse = validaReglasMitras(a, in.getMonto(), in.getFechaOperacion(),operacion.getCuentaBeneficiario());
					    		}else if(matriz.getIdorigen() == 30500) {
					    			valiResponse = validaReglasFama(a_pk, in.getMonto(), in.getFechaOperacion());
					    		}else {
					    			valiResponse.setCodigo(999);
					    		}
								
								    
							                            	//registramos los movimientos a temporal
								log.info("................valid response..............."+valiResponse.getId());
								if(valiResponse.getId() == 999) {
									SpeiTemporal temporal = new SpeiTemporal();
	                            	//Obtengo datos de Auxiliar TDD
	                            	//Auxiliar folio_tdd_auxiliar = auxiliarService.buscarPorId(folioTarjeta.getPk());
	                            	//Buscamos tabla para comision
	                            	TablaPK tb_pk_comision = new TablaPK(idtabla,"monto_comision");
	                            	Tabla tb_comision = tablasService.buscarPorId(tb_pk_comision);
	                            	
	                            	//Buscamos tabla para producto comision
	                                tb_pk_comision = new TablaPK(idtabla,"producto_comision");
	                                Tabla tb_producto_comision = tablasService.buscarPorId(tb_pk_comision);
	                                
		                                //Buscamos tabla para producto iva comision
	                                tb_pk_comision = new TablaPK(idtabla,"producto_iva_comision");
	                                Tabla tb_producto_iva_comision = tablasService.buscarPorId(tb_pk_comision);
	                                						                                
	                                //Buscamos tabla para idcuenta
	                                TablaPK tb_pk_cuenta = new TablaPK(idtabla,"cuenta_contable");
	                                Tabla tb_cuenta_contable = tablasService.buscarPorId(tb_pk_cuenta);
	                                
	                                try {
	                                	//Vamos a registrar movimiento a producto abono(Abono)
		                            	temporal.setIdorigen(a.getIdorigen());
		                            	temporal.setIdgrupo(a.getIdgrupo());
		                            	temporal.setIdsocio(a.getIdsocio());
		                            	temporal.setIdorigenp(a.getAuxiliarPK().getIdorigenp());
		                            	temporal.setIdproducto(a.getAuxiliarPK().getIdproducto());
		                            	temporal.setIdauxiliar(a.getAuxiliarPK().getIdauxiliar());
		                            	temporal.setEsentrada(true);
		                            	temporal.setAcapital(in.getMonto());
		                                temporal.setReferencia(String.valueOf(in.getReferenciaNumerica()));
		                                temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
		                                temporal.setSesion(funcionesSaiService.session());
		                                String sai = funcionesSaiService.sai_auxiliar(new AuxiliarPK(temporal.getIdorigenp(), temporal.getIdproducto(), temporal.getIdauxiliar()));
		                                temporal.setSai_aux(sai);	
		                                temporal.setMov(1);
		                                temporal.setTipopoliza(1);
		                                speiTemporalService.guardar(temporal);
		                                
		                                //Vamos a registrar el movimiento a cuentaContable(Cargo)
		                                temporal = new SpeiTemporal();
		                                temporal.setIdcuenta(tb_cuenta_contable.getDato1());
		                                temporal.setIdorigen(a.getIdorigen());
		                            	temporal.setIdgrupo(a.getIdgrupo());
		                            	temporal.setIdsocio(a.getIdsocio());
		                            	temporal.setEsentrada(false);
		                            	temporal.setAcapital(in.getMonto());
		                                temporal.setReferencia(String.valueOf(in.getReferenciaNumerica()));
		                                temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
		                                temporal.setSesion(funcionesSaiService.session());	
		                                temporal.setMov(2);
		                                temporal.setTipopoliza(1);
		                                speiTemporalService.guardar(temporal);
		                                
		                                								                               
		                                //Vamos a depositar a TDD si el cliente es CSN
		                                if(matriz.getIdorigen() == 30200) {
		                                	log.info("Vamos a depositar a la TDD Alestra");
		                                	valiResponse = validaReglasCsn(a_pk, in.getMonto(),in.getFechaOperacion(),1);
		                                }/*else {
		                                	valiResponse = validaReglasMitras(a_pk, in.getMonto(),in.getFechaOperacion());
		                                }*/
		                              
		                               
		                                if(valiResponse.getId() == 999) {
		                                	//vamos a general poliza(cargo cuenta spei y abono tdd)
		                                	Integer movs_aplicados = funcionesSaiService.aplica_movs(Integer.parseInt(tb_usuario.getDato1()), temporal.getSesion(),1,temporal.getReferencia());
		                                	log.info("total aplicados");
		                                	if(movs_aplicados > 0) {
		                                		
		                                		/*********************Comision************************/
				                                if(Double.parseDouble(tb_comision.getDato1()) > 0) {
				                                	//Vamos a registrar movimiento a producto abono(cargo comision)
			                                		temporal = new SpeiTemporal();
					                            	temporal.setIdorigen(a.getIdorigen());
					                            	temporal.setIdgrupo(a.getIdgrupo());
					                            	temporal.setIdsocio(a.getIdsocio());
					                            	temporal.setIdorigenp(a.getAuxiliarPK().getIdorigenp());
					                            	temporal.setIdproducto(a.getAuxiliarPK().getIdproducto());
					                            	temporal.setIdauxiliar(a.getAuxiliarPK().getIdauxiliar());
					                            	temporal.setEsentrada(false);
					                            	temporal.setAcapital(Double.parseDouble(tb_comision.getDato1()) +(Double.parseDouble(tb_comision.getDato1())) * 0.16);
					                            	temporal.setReferencia(String.valueOf(in.getReferenciaNumerica()));
					                                temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
					                                temporal.setSesion(funcionesSaiService.session());
					                                String sai_c = funcionesSaiService.sai_auxiliar(new AuxiliarPK(temporal.getIdorigenp(), temporal.getIdproducto(), temporal.getIdauxiliar()));
					                                System.out.println(sai_c);
					                                temporal.setSai_aux(sai_c);
					                                temporal.setMov(3);
					                                temporal.setTipopoliza(3);
					                                speiTemporalService.guardar(temporal);
					                                
					                                
					                                
				                                	//Vamos a registrar movimiento al producto comision(abono )
					                                temporal = new SpeiTemporal();
					                                temporal.setIdorigen(a.getIdorigen());
					                            	temporal.setIdgrupo(a.getIdgrupo());
					                            	temporal.setIdsocio(a.getIdsocio());
					                            	temporal.setIdproducto(Integer.parseInt(tb_producto_comision.getDato1()));							                            	
					                            	temporal.setEsentrada(true);
					                            	temporal.setAcapital(Double.parseDouble(tb_comision.getDato1()));									                            	
					                                temporal.setReferencia(String.valueOf(in.getReferenciaNumerica()));
					                                temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
					                                temporal.setSesion(funcionesSaiService.session());	
					                                temporal.setMov(4);
					                                temporal.setTipopoliza(3);
					                                speiTemporalService.guardar(temporal);
					                                
					                                
					                                //Vamos a registrar movimiento al producto iva comision(Abono)
					                                Producto producto_comision = productoService.buscarPorId(Integer.parseInt(tb_producto_comision.getDato1()));
					                                temporal = new SpeiTemporal();
					                                temporal.setIdorigen(a.getIdorigen());
					                            	temporal.setIdgrupo(a.getIdgrupo());
					                            	temporal.setIdsocio(a.getIdsocio());
					                            	temporal.setIdcuenta(producto_comision.getCuentaiva());							                            	
					                            	temporal.setEsentrada(true);
					                            	temporal.setAcapital(Double.parseDouble(tb_comision.getDato1()) * 0.16);									                            	
					                                temporal.setReferencia(String.valueOf(in.getReferenciaNumerica()));
					                                temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
					                                temporal.setSesion(funcionesSaiService.session());	
					                                temporal.setMov(5);
					                                temporal.setTipopoliza(3);
					                                speiTemporalService.guardar(temporal);				                                
					                                
					                                if(matriz.getIdorigen() == 30200) {//Validamos el origen porque si es csn hay que depositarle a TDD
					                                	log.info("Vamos retirar la comision de la TDD");
					                                	valiResponse = validaReglasCsn(a.getAuxiliarPK(),0.0,in.getFechaOperacion(),2);//Double.parseDouble(tb_comision.getDato1())+ (Double.parseDouble(tb_comision.getDato1())) *0.16, ovs_aplicados, movs_aplicados) = consumoCsnTDD.retirarSaldo(tb_url_tdd.getDato2(),tarjeta.getIdtarjeta(),);	
				                                	    if(valiResponse.getId() == 999) {
						                                	movs_aplicados = funcionesSaiService.aplica_movs(Integer.parseInt(tb_usuario.getDato1()), temporal.getSesion(),3,temporal.getReferencia());
						                                	if(movs_aplicados > 0) {
					                                	    	log.info("....uso de tdd activa pero sin conexion a ws.....");
							                                	operacion.setMensajeerror("uso de tdd activa pero sin conexion a ws");
														    	operacion.setResponsecode(35);
														    	abonoSpeiService.guardar(operacion);
							                                	resp.setId(35);
							                                	resp.setMensaje("devolver");
					                                	    }else {
					                                	    	//Aqui hay que realizar depositar la comision que se retiro
					                                	    	log.info(".........Se volvio a retirar de la TDD falla en SAICoop.......");
					                                	    	valiResponse = validaReglasCsn(a.getAuxiliarPK(),0.0,in.getFechaOperacion(),3);
					                                	    }
				                                	    }
					                                	
					                                }else {
					                                	valiResponse.setId(999);
					                                }					                               
				                                }
				                                
				                                
				                                if(valiResponse.getId() == 999) {
				                                	resp.setMensaje("confirmar");
			                                		operacion.setAplicado(true);
			                                		operacion.setFechaProcesada(new Date());
											    	operacion.setResponsecode(000);
											    	resp.setCodigo(200);
											    	abonoSpeiService.guardar(operacion);
				                                }
		                                	 }else {		                                	    
		                                	    if(matriz.getIdorigen() == 30200) {
		                                	    	log.info(".........Se volvio a retirar de la TDD falla en SAICoop.......");
		                                	    	valiResponse = validaReglasCsn(a_pk, in.getMonto(),in.getFechaOperacion(),2);
		                                	    }
		                                	    //bandera = consumoCsnTDD.retirarSaldo(tb_url_tdd.getDato2(),tarjeta.getIdtarjeta(),in.getMonto());
		                                	    operacion.setMensajeerror("Falla al procesar en SAICoop");
										    	operacion.setResponsecode(35);
										    	abonoSpeiService.guardar(operacion);
		                                	    resp.setId(35);
		                                	    resp.setMensaje("devolver");
		                                	}								                                	
		                                }else {
		                                	log.info("Vali response:"+valiResponse);
		                                	log.info("Mensaje ne la validacion:"+valiResponse.getMensaje());
		                                	operacion.setMensajeerror(valiResponse.getMensaje());
									    	operacion.setResponsecode(valiResponse.getId());
									    	abonoSpeiService.guardar(operacion);
			                            	resp.setMensaje("devolver");
			                            	resp.setId(valiResponse.getId());
		                                }
		                                speiTemporalService.eliminar(temporal.getSesion(),in.getReferenciaNumerica());
	                                  }catch(Exception e){
	                                	speiTemporalService.eliminar(temporal.getSesion(),in.getReferenciaNumerica());
	                                	log.info("________________Error al procesar spei____________________:"+e.getMessage());
	                                  }				
								    }else {
									 operacion.setMensajeerror(resp.getMensaje());
							    	 operacion.setResponsecode(valiResponse.getId());
							    	 abonoSpeiService.guardar(operacion);
	                            	 resp.setMensaje("devolver");
	                            	 resp.setId(valiResponse.getId());
								    }
								
							     }else {
								     //resp.setMensaje("No existen registros para la cuenta:"+in.getCuentaBeneficiario());
							       log.info("........No existen registros para la cuenta clabe:"+in.getCuentaBeneficiario()+".........");
							       operacion.setMensajeerror("No existen registros para la clabe:");
						    	   operacion.setResponsecode(1);
						    	   abonoSpeiService.guardar(operacion);
							       resp.setMensaje("devolver");
								   resp.setId(5);
							    }
							      break;
					        	default:
							      break;
						}//Fin de switch	    			
	    	
	    	}else {
	    		
	    	  	//resp.setMensaje("Estatus no valido para operar para origen:"+origen_usuario.getIdorigen());
	    		log.info(".............Estatus no valido para operar para origen:"+origen_usuario.getIdorigen()+"...........");
	    		resp.setMensaje("devolver");
	    	  	resp.setId(3);
	    	}
	    }else {
	    	//resp.setMensaje("Operacion fuera de horario");
	    	log.info(".......Operacion fuera de Horario.......");
	    	operacion.setMensajeerror("Operacion fuera de Horario");
			operacion.setResponsecode(1);
			abonoSpeiService.guardar(operacion);
	    	resp.setMensaje("devolver");
	    	resp.setId(2);
	      }
	    }else {
	    	log.info("..............Operacion duplicada1............"+in.getId());
	    	resp.setId(1);
	    }
	    
		return resp; 
		
	}
		
	public ConcResultadoVo conciliacion(Integer page,String tipoOrden,Integer fecha) {
        ConcResultadoVo resultado = new ConcResultadoVo();
        try {
			//Buscamos la empresa
        	TablaPK tbPk = new TablaPK("stp","empresa");
        	Tabla tabla = tablasService.buscarPorId(tbPk);
        	ConcPeticionVo conciliacionPet = new ConcPeticionVo();
        	conciliacionPet.setPage(page);
        	conciliacionPet.setTipoOrden(tipoOrden);
        	conciliacionPet.setFecha(fecha);
        	if(tabla != null) {
        		conciliacionPet.setEmpresa(tabla.getDato1());
        		String firma = firmaPeticion(1,conciliacionPet,null,"");
        		conciliacionPet.setFirma(firma);
        		
        		String peticion = json.toJson(conciliacionPet);
        		String resultadoConciliacion  = httpMethods.conciliacion(peticion);
        		resultado = json.fromJson(resultadoConciliacion,ConcResultadoVo.class);
        		if(resultado.getEstado() == 0) {
        			resultado.setCodigo(200);
        		}else {
        			resultado.setCodigo(400);
        		}
        	}else {
        		log.info(".............Empresa no definida............");
        		resultado.setCodigo(400);
        		resultado.setMensaje("Empresa no definida");
        	}
		} catch (Exception e) {
		    log.info("Error al obtener conciliacion:"+e.getMessage());
		}
		return resultado;
	}
	
	public ConcResultadoVo conciliacionHis(ConcPeticionVo conciliacionPet) {
	    
		return null;
	}
	
	public SaldoResultadoVo consultaSaldo(String clabes,String fecha) {
		SaldoResultadoVo resultadoConsulta = new SaldoResultadoVo();
		try {
			ConsultaSaldoPet peticion = new ConsultaSaldoPet();

			TablaPK tbPk = new TablaPK("stp","cuenta_concentradora");
			Tabla tablaCuentaConcentradora = tablasService.buscarPorId(tbPk);
		
			if(tablaCuentaConcentradora != null) {
				tbPk = new TablaPK("stp","empresa");
	        	Tabla tabla = tablasService.buscarPorId(tbPk);
				peticion.setCuentaOrdenante(tablaCuentaConcentradora.getDato1().trim());
				peticion.setEmpresa(tabla.getDato1());
        		String firma = firmaPeticion(2,null,peticion,fecha);
        		peticion.setFirma(firma);
        		
        		String peticionHttp = json.toJson(peticion);
        		String resultadoConsultaHttp  = httpMethods.consultaSaldo(peticionHttp);
        		resultadoConsulta = json.fromJson(resultadoConsultaHttp,SaldoResultadoVo.class);
        		if(resultadoConsulta.getEstado() == 0) {
        			resultadoConsulta.setCodigo(200);
        		}else {
        			resultadoConsulta.setCodigo(400);
        		}
			}else {
				log.info("...............Clabe no existe...............");
				resultadoConsulta.setMensaje("Clabe no existe");
				resultadoConsulta.setEstado(999);
				resultadoConsulta.setCodigo(400);
			}			
		} catch (Exception e) {
			log.info("Error al consultar saldo");
			resultadoConsulta.setMensaje("Se produjo un error general");
		}
		return resultadoConsulta;
	}
	

    private response validaReglasCsn(AuxiliarPK opa,Double monto,Integer fechaOperacion,Integer tipoOperacion) {
		response response = new response();
		response.setId(0);
		response.setMensaje("Error General");
		response.setCodigo(400);
		try {
			//Se han preparado los movimientos es hora de enviar a la tdd
            //Buscamos la tabla donde esta la url
            TablaPK pk_url_tdd = new TablaPK(idtabla,"url_tdd");
            Tabla tb_url_tdd = tablasService.buscarPorId(pk_url_tdd);
            Origen matriz = origenesService.buscarMatriz();
            //Vamos a buscar si esta la tdd
            TablaPK pk_uso_tdd = null;
            Tabla tb_uso_tdd = null;
            FolioTarjeta folioTarjeta = null;
            Tarjeta tarjeta = null;
            boolean bandera = false;
            switch(tipoOperacion) {
              case 0://Validacion de montos maximos y minimos
            	//Buscamos minimo y maximo a operar
  	    		TablaPK tb_pk= new TablaPK(idtabla,"monto_minimo");
  	    		Tabla tb_minimo = tablasService.buscarPorId(tb_pk);
  	    		tb_pk.setIdElemento("monto_maximo");
  	    		Tabla tb_maximo = tablasService.buscarPorId(tb_pk);
  	    		Double monto_minimo = new Double(tb_minimo.getDato1());
  	    		Double monto_maximo = new Double(tb_maximo.getDato1());
  	    		if(monto >= monto_minimo) {
  	    			if(monto <= monto_maximo) {
  	    			  folioTarjeta = folioTarjetaService.buscarPorId(opa);
  					  if(folioTarjeta != null) {
  						//Buscamos registro para tarjeta
  			        	 tarjeta = tarjetaService.buscarPorId(folioTarjeta.getIdtarjeta());
  			        	 if(tarjeta != null) {
  			        	    //Validamos fecha de vencimiendo
  			        	    if(tarjeta.getFecha_vencimiento().after(matriz.getFechatrabajo())) {
  			        	    	tb_pk.setIdElemento("monto_maximo_diario");
								Tabla tb_monto_maximo_diario = tablasService.buscarPorId(tb_pk);
	                            List<AbonoSpei>abonos = abonoSpeiService.todasPorFecha(fechaOperacion);
	                            Double acumulado = 0.0;
	                            for(int i=0;i<abonos.size();i++) {
	                            	acumulado = acumulado + abonos.get(i).getMonto();
	                            }
	                            if((acumulado + monto) < new Double(tb_monto_maximo_diario.getDato1())) {
	                            	response.setMensaje("OK");
	                            	response.setId(999);
	                            	response.setCodigo(200);
	                            }else {
	                            	log.info("..........el monto operado hoy supera el permitido en el core..........");
	                            	response.setMensaje("El monto operado hoy supera el permitido en el core");
	                            	response.setId(25);
	                            }
  			        	   }else {
  			        		   log.info("..............Tarjeta de debito esta vencida...........");
  			        		   response.setMensaje("Tarjeta de Debito esta vencida");
  			        		   response.setId(15);
  			        	   }
  			        	}else {
  			        		log.info(".............Tarjeta de Debito no encontrada..............");
  			        		response.setMensaje("Tarjeta de Debito no encontrada");
  			        		response.setId(15);
  			        		
  			        	}
  					  } else {
  					     log.info("............Sin registros para TDD...........");
  					     response.setMensaje("Sin registros para TDD");
  					     response.setId(14); 
  					  } 
  	    			}else {
  	    				log.info(".........Monto es mayor al permitido en el core........");
  	    				response.setMensaje("Monto es mayor al permitido en el core");
  	    				response.setId(25);
  	    			}
  	    		}else {
  	    			log.info(".........Monto es menor al permitido en el core........");
  	    			response.setMensaje("Monto es menor al permitido en el core");
  	    			response.setId(25);
  	    		}
            	break;
              case 1:
            	    pk_uso_tdd = new TablaPK(idtabla,"activar_desactivar_tdd");
				    tb_uso_tdd = tablasService.buscarPorId(pk_uso_tdd);
				    if(tb_uso_tdd.getDato1().equals("1")) {
				    	folioTarjeta = folioTarjetaService.buscarPorId(opa);
				    	tarjeta = tarjetaService.buscarPorId(folioTarjeta.getIdtarjeta());
				    	bandera = consumoCsnTDD.depositarSaldo(tb_url_tdd.getDato2(),tarjeta.getIdtarjeta(),monto);
				    	if(bandera) {
				    		response.setId(999);
				    		response.setMensaje("OK");
				    	}else {
				    		response.setId(23);
				    		response.setMensaje("Error al realizar deposito a TDD");
				    		log.info("..............Error al realizar deposito a TDD..........");
				    	}
				    }else {
				    	response.setId(23);
				    	response.setMensaje("Asegurate de activar servicios de Tarjeta de Debito");
				    	log.info("Asegurate de activar los servicios de Tarjeta de Debito");
				    }
            	  break;
			  case 2:
				    pk_uso_tdd = new TablaPK(idtabla,"activar_desactivar_tdd");
				    tb_uso_tdd = tablasService.buscarPorId(pk_uso_tdd);
				    if(tb_uso_tdd.getDato1().equals("1")) {
				    	folioTarjeta = folioTarjetaService.buscarPorId(opa);
				    	tarjeta = tarjetaService.buscarPorId(folioTarjeta.getIdtarjeta());
				    	bandera = consumoCsnTDD.retirarSaldo(tb_url_tdd.getDato2(),tarjeta.getIdtarjeta(),monto);
				    	if(bandera) {
				    		response.setId(999);
				    		response.setMensaje("OK");
				    	}else {
				    		response.setId(23);
				    		response.setMensaje("Error al realizar retiro a TDD");
				    		log.info("..............Error al realizar retiro a TDD..........");
				    	}
				    }else {
				    	response.setId(23);
				    	response.setMensaje("Asegurate de activar servicios de Tarjeta de Debito");
				    	log.info("Asegurate de activar los servicios de Tarjeta de Debito");
				    }
				  break;
				
			 }
            
		} catch (Exception e) {
			log.info("....Error al validar reglas CSN..."+e.getMessage());
			response.setMensaje("Error al validar reglas CSN");
		}
		return response;
	}
 
	private response validaReglasMitras(Auxiliar a,Double monto,Integer fechaOperacion,String clabeBeneficiario) {
		response response = new response();
		response.setId(0);
		response.setMensaje("Error General");
		response.setCodigo(400);
		try {
			    //Validamos que la persona se encuentre en los grupos validos
			    PersonaPK personaPk = new PersonaPK(a.getIdorigen(),a.getIdgrupo(),a.getIdsocio());
			    Persona persona = personaService.buscarPorId(personaPk);
			    
			    if(persona.getPk().getIdgrupo() == 10 || persona.getPk().getIdgrupo() == 12) {
			    	//buscamos si esta en elgrupo 88 personas 
			    	persona = personaService.buscarPorCurpGrupo(persona.getCurp(),88);
			    	if(persona == null) {
			    		//Confirmamo que siga bloqueado en sopar
			    		Sopar sopar = soparService.buscarPorIdTipo(personaPk, "lista_personas_bloqueadas_cnbv");
			    		if(sopar == null) {
			    			//Buscamos minimo y maximo a operar
			  	    		TablaPK tb_pk= new TablaPK(idtabla,"monto_minimo");
			  	    		Tabla tb_minimo = tablasService.buscarPorId(tb_pk);
			  	    		tb_pk.setIdElemento("monto_maximo");
			  	    		Tabla tb_maximo = tablasService.buscarPorId(tb_pk);
			  	    		Double monto_minimo = new Double(tb_minimo.getDato1());
			  	    		Double monto_maximo = new Double(tb_maximo.getDato1());
			  	    		
			  	    		if(monto >= monto_minimo) {
			  	    			if(monto <= monto_maximo) {
			  	    			  //Buscamos la configuracion de producto para abono
									tb_pk.setIdElemento("producto_abono");
									Tabla tabla_producto_abono = tablasService.buscarPorId(tb_pk);
									if(tabla_producto_abono != null){
										//Validamos que el producto para abono configurado en tablas sea el mismo relacionado a la clabe
										Producto producto_abono = productoService.buscarPorId(a.getAuxiliarPK().getIdproducto());
										
										if(producto_abono != null){
											tb_pk.setIdElemento("monto_maximo_diario");
											Tabla tb_monto_maximo_diario = tablasService.buscarPorId(tb_pk);
											List<AbonoSpei>abonos = abonoSpeiService.todasPorFecha(fechaOperacion);
											Double acumulado = 0.0;
											for(int i=0;i<abonos.size();i++) {
												acumulado = acumulado + abonos.get(i).getMonto();
											}
											if((acumulado + monto) < new Double(tb_monto_maximo_diario.getDato1())) {
												log.info(String.valueOf(fechaOperacion).substring(0,6));
												Double totalMes = abonoSpeiService.totalMes(clabeBeneficiario,String.valueOf(fechaOperacion).substring(0,6));
												System.out.println("Total acumulado en el mes:"+totalMes+",SELECT SUM(monto) FROM speirecibido WHERE LEFT(fechaoperacion::TEXT, 6) ="+String.valueOf(fechaOperacion).substring(1,6)+" AND cuentabeneficiario= "+clabeBeneficiario+" AND aplicado=true");
												tb_pk.setIdElemento("maximo_mes");
												Tabla tb_monto_maximo_mes = tablasService.buscarPorId(tb_pk);
												if(totalMes <= Double.parseDouble(tb_monto_maximo_mes.getDato1())){
													response.setMensaje("OK");
													response.setId(999);
												}else{
													log.info("..........Limite mensual alcanzado..........");
													response.setMensaje("Ha alcanzado el limite mensual en el core : $"+tb_monto_maximo_mes.getDato1());
													response.setId(17);
												}
											}else {
												log.info("..........el monto operado hoy supera el permitido en el core..........");
												response.setMensaje("El monto operado hoy supera el permitido en el core");
												response.setId(16);									
											}
										}else{
												log.info("..........Producto configurado como abono en tablas no corresponde a vinculado en clabes..........");
												response.setMensaje("Producto configurado como abono en tablas no corresponde a vinculado en clabes");
												response.setId(15);
										}
									}else {
										log.info("..........No existe configuracion de producto abono..........");
										response.setMensaje("No existe configuracion de producto abono");
										response.setId(14);
									}
			  	    			}else {
			  	    				log.info(".........Monto es mayor al permitido en el core........");
			  	    				response.setMensaje("Monto es mayor al permitido en el core");
			  	    				response.setId(13);
			  	    			}
			  	    		}else {
			  	    			log.info(".........Monto es menor al permitido en el core........");
			  	    			response.setMensaje("Monto es menor al permitido en el core");
			  	    			response.setId(6);
			  	    		}
			    		}else {
			    			log.info(".........Socio bloqueado por CNBV........");
		  	    			response.setMensaje("Socio bloqueado por CNBV");
		  	    			response.setId(18);
			    		}
			    	}else {			    	
			    		log.info("Socio bloqueado,grupo 88");
			    	}
			    }else {
			    	log.info(".........Grupo para socio no permitido en el core........");
  	    			response.setMensaje("Socio perteneciente a grupo no permitido en el core");
  	    			response.setId(19);
			    }
			    
		} catch (Exception e) {
			log.info("....Error al validar reglas Mitras..."+e.getMessage());
			response.setMensaje("Error al validar reglas Mitras");
		}
		return response;
	}
	
	
	private response validaReglasFama(AuxiliarPK opa,Double monto,Integer fechaOperacion) {
		response response = new response();
		response.setId(0);
		response.setMensaje("Error General");
		response.setCodigo(400);
		try {
		       	//Buscamos minimo y maximo a operar
  	    		TablaPK tb_pk= new TablaPK(idtabla,"monto_minimo");
  	    		Tabla tb_minimo = tablasService.buscarPorId(tb_pk);
  	    		tb_pk.setIdElemento("monto_maximo");
  	    		Tabla tb_maximo = tablasService.buscarPorId(tb_pk);
  	    		Double monto_minimo = new Double(tb_minimo.getDato1());
  	    		Double monto_maximo = new Double(tb_maximo.getDato1());
  	    		if(monto >= monto_minimo) {
  	    			if(monto <= monto_maximo) {
  	    			  //Buscamos la configuracion de producto para abono
						tb_pk.setIdElemento("producto_abono");
						Tabla tabla_producto_abono = tablasService.buscarPorId(tb_pk);
						if(tabla_producto_abono != null){
							//Validamos que el producto para abono configurado en tablas sea el mismo relacionado a la clabe
							Producto producto_abono = productoService.buscarPorId(opa.getIdproducto());
							if(producto_abono != null){
								//tb_pk.setIdElemento("monto_maximo_diario");
								//Tabla tb_monto_maximo_diariod = tablasService.buscarPorId(tb_pk);
								//List<AbonoSpei>abonos = abonoSpeiService.todasPorFecha(fechaOperacion);
								//Double acumulado = 0.0;
								//for(int i=0;i<abonos.size();i++) {
									//acumulado = acumulado + abonos.get(i).getMonto();
							//	}
								//if((acumulado + monto) < new Double(tb_monto_maximo_diario.getDato1())) {
									response.setMensaje("OK");
									response.setId(999);
							/*	}else {
									log.info("..........el monto operado hoy supera el permitido en el core..........");
									response.setMensaje("El monto operado hoy supera el permitido en el core");
									response.setId(25);									
								}*/
							}else{
									log.info("..........Producto configurado como abono en tablas no corresponde a vinculado en clabes..........");
									response.setMensaje("Producto configurado como abono en tablas no corresponde a vinculado en clabes");
									response.setId(15);
							}
						}else {
							log.info("..........No existe configuracion de producto abono..........");
							response.setMensaje("No existe configuracion de producto abono");
							response.setId(14);
						}
  	    			}else {
  	    				log.info(".........Monto es mayor al permitido en el core........");
  	    				response.setMensaje("Monto es mayor al permitido en el core");
  	    				response.setId(13);
  	    			}
  	    		}else {
  	    			log.info(".........Monto es menor al permitido en el core........");
  	    			response.setMensaje("Monto es menor al permitido en el core");
  	    			response.setId(6);
  	    		}
		} catch (Exception e) {
			log.info("....Error al validar reglas Mitras..."+e.getMessage());
			response.setMensaje("Error al validar reglas Mitras");
		}
		return response;
	}
 
	private String firmaPeticion(Integer operacion,ConcPeticionVo conciliacion,ConsultaSaldoPet consultaSaldo,String fecha) {
		String firmada = "";
		try {
			 StringBuilder sB = new StringBuilder();
			 if(operacion ==1 ) {
				sB.append("||");
		        sB.append(conciliacion.getEmpresa()).append("|");
		        sB.append(conciliacion.getTipoOrden()).append("|");
		        sB.append("||");
			 }else if(operacion == 2) {
				sB.append("||");
			    sB.append(consultaSaldo.getEmpresa()).append("|");
			    sB.append(consultaSaldo.getCuentaOrdenante()).append("|");
			    sB.append(fecha);
			    sB.append("||"); 
			    
			    log.info(sB.toString());
			 }
		     String cadena = sB.toString();
		     firmada = sign(cadena);
		     log.info("La firma es:"+firmada);
		} catch (Exception e) {
			log.info("Error al firmar peticion:"+e.getMessage());
		}
		return firmada;
	}
	
	// Consigo mi firma
	public String sign(String cadena) throws Exception {
		String firmaCod;
		// Direccion de mi keystore local
		String fileName = ruta()+ System.getProperty("file.separator")+"mitras"+System.getProperty("file.separator")+ "caja_mitras.jks";//"/claves/cajamitras.jks";
		String password =  "fenoreste2024";//"12345678";//"fenoreste2023";
		String alias = "caja_mitras";
		try {
			String data = cadena;
			Signature firma = Signature.getInstance("SHA256withRSA");
			RSAPrivateKey llavePrivada = getCertified(fileName, password, alias);
			firma.initSign(llavePrivada);
			byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
			firma.update(bytes, 0, bytes.length);
			Base64.Encoder encoder = Base64.getEncoder();
			firmaCod = encoder.encodeToString(firma.sign());			
		} catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException e) {
			throw new Exception("Exceptions" + e.getMessage(), e.getCause());
		}
		return firmaCod;
	}

	private RSAPrivateKey getCertified(String keystoreFilename, String password, String alias) throws Exception {
		RSAPrivateKey privateKey;
		try {
			KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(new FileInputStream(keystoreFilename), password.toCharArray());
			privateKey = (RSAPrivateKey) keystore.getKey(alias, password.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | IOException
				| CertificateException ex) {
			throw new Exception("Exception" + ex.getMessage(), ex.getCause());
		}
		return privateKey;
	}

	//Parao obtener la ruta del servidor
    public static String ruta() {
        String home = System.getProperty("user.home");
        String separador = System.getProperty("file.separator");
        String actualRuta = home + separador + "CaSpei" + separador;
        return actualRuta;
    }

	
	
}
