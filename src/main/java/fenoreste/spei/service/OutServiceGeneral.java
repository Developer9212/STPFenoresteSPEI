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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import fenoreste.spei.entity.Auxiliar;
import fenoreste.spei.entity.Banco;
import fenoreste.spei.entity.ClabeInterbancaria;
import fenoreste.spei.entity.Persona;
import fenoreste.spei.entity.PersonaPK;
import fenoreste.spei.entity.Tabla;
import fenoreste.spei.entity.TablaPK;
import fenoreste.spei.modelos.OrdenVo;
import fenoreste.spei.modelos.STPDispersionVo;
import fenoreste.spei.modelos.DispersionVo;
import fenoreste.spei.modelos.STPOrderVo;
import fenoreste.spei.modelos.STPResultado;
import fenoreste.spei.stp.HttpMethods;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OutServiceGeneral {

	@Autowired
	private IClabeInterbancariaService clabeInterbancariaService;
	@Autowired
	private ITablaService tablaService;
	@Autowired
	private IBancoService bancoService;
	@Autowired
	private IAuxiliarService auxiliarService;
	@Autowired
	private IPersonaService personaService;
	@Autowired
	private HttpMethods httpMethods;

	private String idtabla = "spei_salida";
    
	Gson json = new Gson();
	public DispersionVo sendOrder(OrdenVo order) {
		DispersionVo dispersion = new DispersionVo();
		try {
			// Vamos a buscar la clabe
			ClabeInterbancaria clabe = clabeInterbancariaService.buscarPorClabe(order.getCuentaOrdenante());
			if (clabe != null) {
				// Validamos monto
				TablaPK tablaPk = new TablaPK(idtabla, "monto_maximo");
				Tabla tabla = tablaService.buscarPorId(tablaPk);
				if (tabla != null) {
					if (order.getMonto() <= Double.parseDouble(tabla.getDato1())) {
						// monto minimo
						tablaPk = new TablaPK(idtabla, "monto_minimo");
						tabla = tablaService.buscarPorId(tablaPk);
						if (tabla != null) {
							if (order.getMonto() >= Double.parseDouble(tabla.getDato1())) {
								// Institutcion contraparte(Banco receptor)
								Banco banco = bancoService.buscarNombre(order.getInstitucionContraparte().trim());
								if (banco != null) {
									// Complementamos la orden
									order.setInstitucionContraparte(String.valueOf(banco.getIdbanco()));
									STPDispersionVo response = enviarOrden(order);
									log.info("Ressssspnseeeeeee:"+response);
									dispersion.setCodigo(200);
									dispersion.setMensaje(response.getResultado().getDescripcionError());
									dispersion.setFechaOperacion(new Date().toLocaleString());
									dispersion.setIdOrden(response.getResultado().getId());
								} else {
									log.info("......Banco receptor no existe:" + order.getInstitucionContraparte()+ ".......");
									dispersion.setCodigo(400);
									dispersion.setMensaje("Banco receptor no existe:" + order.getInstitucionContraparte());
								}
							} else {
								log.info("......Monto esta por debajo al permitido en el core......");
								dispersion.setCodigo(400);
								dispersion.setMensaje("Monto esta por debajo al permitido en el core");
							}
						} else {
							log.info("......Se requiere configuracion de monto minimo.....");
							dispersion.setCodigo(400);
							dispersion.setMensaje("Se requiere configuracion de monto minimo");
						}
					} else {
						log.info(".....Monto es mayor al permitido en el core......");
						dispersion.setCodigo(400);
						dispersion.setMensaje("Monto es mayor al permitido en el core");
					}
				} else {
					log.info("......Se requiere configuracion de monto maximo......");
					dispersion.setCodigo(400);
					dispersion.setMensaje("Se requiere configuracion de monto maximo");
				}
			} else {
				log.info("..........La clabe para dispersar no existe.........");
				dispersion.setCodigo(400);
				dispersion.setMensaje("La clabe para dispersar no existe");
			}
		} catch (Exception e) {
			log.info("Error al enviar orden:" + e.getMessage());
		}
		return dispersion;
	}

	private STPDispersionVo enviarOrden(OrdenVo orden) {
		STPDispersionVo response = new STPDispersionVo();
		try {
			STPOrderVo ordenSTP = new STPOrderVo();
			ordenSTP.setClaveRastreo(generarCadenaAlfanumerica(8));
			ordenSTP.setConceptoPago(orden.getConceptoPago());
			ordenSTP.setTipoCuentaOrdenante(orden.getCuentaOrdenante());
			ordenSTP.setCuentaBeneficiario(orden.getCuentaBeneficiario());
			TablaPK tbPk = new TablaPK(idtabla, "empresa");
			Tabla tabla = tablaService.buscarPorId(tbPk);

			ordenSTP.setEmpresa("");// Solicitarlo a Caja
			ordenSTP.setInstitucionContraparte(orden.getInstitucionContraparte());
			ordenSTP.setInstitucionOperante("");// Solicitarlo a caja
			ordenSTP.setMonto(String.valueOf(orden.getMonto()));
			ordenSTP.setNombreBeneficiario(orden.getNombreBeneficiario());
			ClabeInterbancaria clabe = clabeInterbancariaService.buscarPorClabe(orden.getCuentaOrdenante());
			Auxiliar auxiliar = auxiliarService.buscarPorId(clabe.getAuxPk());
			PersonaPK personaPk = new PersonaPK(auxiliar.getIdorigen(), auxiliar.getIdgrupo(), auxiliar.getIdsocio());
			Persona persona = personaService.buscarPorId(personaPk);
			ordenSTP.setNombreOrdenante(
					persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
			ordenSTP.setReferenciaNumerica(generarCadenaAlfanumerica(10));
			ordenSTP.setRfcCurpBeneficiario(orden.getRfcCurpBeneficiario());
			ordenSTP.setRfcCurpOrdenante(persona.getCurp());
			ordenSTP.setTipoCuentaBeneficiario("40");
			ordenSTP.setTipoCuentaOrdenante("40");
			ordenSTP.setTipoPago("1");
			ordenSTP.setFirma(firmarOrden(ordenSTP));
			
			String peticion = json.toJson(ordenSTP);
			String respuestaOrden = httpMethods.enviarOrdenSpei(peticion);
			log.info("Respuesta STP:"+respuestaOrden);
			
			Gson json = new Gson();
			response = json.fromJson(respuestaOrden,STPDispersionVo.class);
			log.info("Response STP:"+response);
			
		} catch (Exception e) {
            log.info("Error al enviar orden SPEI:"+e.getMessage());
		}

		return response;

	}

	private String firmarOrden(STPOrderVo orden) {
		String firmada = "";
		try {
			 StringBuilder sB = new StringBuilder();
		            sB.append("||");
		            sB.append(orden.getInstitucionContraparte()).append("|");
		            sB.append(orden.getEmpresa()).append("|");/*
		            sB.append(orden.getFechaOperacion() == null ? "" : orden.getFechaOperacion()).append("|");
		            sB.append(orden.getFolioOrigen() == null ? "" : orden.getFolioOrigen()).append("|");*/
		            sB.append(orden.getClaveRastreo() == null ? "" : orden.getClaveRastreo()).append("|");
		            sB.append(orden.getInstitucionOperante() == null ? "" : orden.getInstitucionOperante()).append("|");
		            sB.append(orden.getMonto() == null ? "" : orden.getMonto()).append("|");
		            sB.append(orden.getTipoPago() == null ? "" : orden.getTipoPago()).append("|");
		            sB.append(orden.getTipoCuentaOrdenante() == null ? "" : orden.getTipoCuentaOrdenante()).append("|");
		            sB.append(orden.getNombreOrdenante() == null ? "" : orden.getNombreOrdenante()).append("|");
		            sB.append(orden.getCuentaOrdenante() == null ? "" : orden.getCuentaOrdenante()).append("|");
		            sB.append(orden.getRfcCurpOrdenante() == null ? "" : orden.getRfcCurpOrdenante()).append("|");
		            sB.append(orden.getTipoCuentaBeneficiario() == null ? "" : orden.getTipoCuentaBeneficiario()).append("|");
		            sB.append(orden.getNombreBeneficiario() == null ? "" : orden.getNombreBeneficiario()).append("|");
		            sB.append(orden.getCuentaBeneficiario() == null ? "" : orden.getCuentaBeneficiario()).append("|");
		            sB.append(orden.getRfcCurpBeneficiario() == null ? "" : orden.getRfcCurpBeneficiario()).append("|");
		            /*sB.append(orden.getEmailBeneficiario() == null ? "" : orden.getEmailBeneficiario()).append("|");
		            sB.append(orden.getTipoCuentaBeneficiario2() == null ? "" : orden.getTipoCuentaBeneficiario2()).append("|");
		            sB.append(orden.getNombreBeneficiario2() == null ? "" : orden.getNombreBeneficiario2()).append("|");
		            sB.append(orden.getCuentaBeneficiario2() == null ? "" : orden.getCuentaBeneficiario2()).append("|");
		            sB.append(orden.getRfcCurpBeneficiario2() == null ? "" : orden.getRfcCurpBeneficiario2()).append("|");
		            sB.append(orden.getConceptoPago() == null ? "" : orden.getConceptoPago()).append("|");
		            sB.append(orden.getConceptoPago2() == null ? "" : orden.getConceptoPago2()).append("|");
		            sB.append(orden.getClaveCatUsuario1() == null ? "" : orden.getClaveCatUsuario1()).append("|");
		            sB.append(orden.getClaveCatUsuario2() == null ? "" : orden.getClaveCatUsuario2()).append("|");
		            sB.append(orden.getClavePago() == null ? "" : orden.getClavePago()).append("|");
		            sB.append(orden.getReferenciaCobranza() == null ? "" : orden.getReferenciaCobranza()).append("|");
		            sB.append(orden.getReferenciaNumerica() == null ? "" : orden.getReferenciaNumerica()).append("|");
		            sB.append(orden.getTipoOperacion() == null ? "" : orden.getTipoOperacion()).append("|");
		            sB.append(orden.getTopologia() == null ? "" : orden.getTopologia()).append("|");
		            sB.append(orden.getUsuario() == null ? "" : orden.getUsuario()).append("|");
		            sB.append(orden.getMedioEntrega() == null ? "" : orden.getMedioEntrega()).append("|");
		            sB.append(orden.getPrioridad() == null ? "" : orden.getPrioridad()).append("|");
		            sB.append(orden.getIva() == null ? "" : orden.getIva()).append("||");*/

		            String cadena = sB.toString();
		            firmada = sign(cadena);
		} catch (Exception e) {
			log.info("Error al firmar orden:"+e.getMessage());
		}
		return firmada;
	}

	// Consigo mi firma
	public String sign(String cadena) throws Exception {
		String firmaCod;
		// Direccion de mi keystore local
		String fileName = ruta() + "fenoreste.jks";
		String password = "fenoreste2023";
		String alias = "fenoreste";
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

	private String generarCadenaAlfanumerica(int longitud) {
		String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder cadenaGenerada = new StringBuilder();

		SecureRandom random = new SecureRandom();
		for (int i = 0; i < longitud; i++) {
			int indice = random.nextInt(caracteres.length());
			char caracter = caracteres.charAt(indice);
			cadenaGenerada.append(caracter);
		}

		return cadenaGenerada.toString();
	}
	
	//Parao obtener la ruta del servidor
    public static String ruta() {
        String home = System.getProperty("user.home");
        String separador = System.getProperty("file.separator");
        String actualRuta = home + separador + "emisor-adquiriente" + separador + "cert-properties" + separador;
        return actualRuta;
    }

}
