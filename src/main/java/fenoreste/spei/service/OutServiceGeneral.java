package fenoreste.spei.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

import com.google.gson.JsonObject;
import fenoreste.spei.entity.*;
import fenoreste.spei.modelos.*;
import fenoreste.spei.util.Util;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import fenoreste.spei.stp.HttpMethods;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;

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
    private IFuncionesSaiService funcionesSaiService;

    @Autowired
    private Util util;

    @Autowired
    private HttpMethods httpMethods;

    private String idtabla = "spei_salida";

    Gson json = new Gson();

    public ResponseLocalDispersionVo sendOrder(RequestLocalDispersionVo order) {
        ResponseLocalDispersionVo dispersion = new ResponseLocalDispersionVo();
        try {
            // Vamos a buscar la clabe
            OpaDTO opa = util.opa(order.getOpaCliente());
            //Buscamos el auxiliar
            AuxiliarPK auxiliarPK = new AuxiliarPK(opa.getIdorigenp(), opa.getIdproducto(), opa.getIdauxiliar());
            Auxiliar a = auxiliarService.buscarPorId(auxiliarPK);
            if (a != null) {
                if (a.getEstatus() == 2) {
                    //Buscamos el producto configurado para operar dispersion
                    TablaPK tbpk = new TablaPK("bankingly_banca_movil", "producto_tdd");
                    Tabla tabla = tablaService.buscarPorId(tbpk);
                    if (tabla != null) {
                        if (Integer.parseInt(tabla.getDato1()) == opa.getIdproducto()) {
                            ClabeInterbancaria clabe = clabeInterbancariaService.buscarPorId(a.getAuxiliarPK());
                            if (clabe != null) {
                                // Validamos monto
                                tbpk = new TablaPK(idtabla, "monto_maximo");
                                tabla = tablaService.buscarPorId(tbpk);
                                if (tabla != null) {
                                    if (order.getMonto() <= Double.parseDouble(tabla.getDato1())) {
                                        // monto minimo
                                        tbpk = new TablaPK(idtabla, "monto_minimo");
                                        tabla = tablaService.buscarPorId(tbpk);
                                        if (tabla != null) {
                                            if (order.getMonto() >= Double.parseDouble(tabla.getDato1())) {
                                                // Institutcion contraparte(Banco receptor)
                                                Banco banco = bancoService.buscarNombre(order.getInstitucionContraparte().trim());
                                                if (banco != null) {
                                                    //Validamos y creamos la orden a enviar
                                                    OrdenPagoWS ordenValida = validarFormarOrdenPago(order);
                                                    if (ordenValida.getCodigo() == 200) {
                                                        // Complementamos la orden
                                                        ordenValida.setInstitucionContraparte(banco.getIdbanco());
                                                        JSONObject ordenHTTP = new JSONObject();
                                                        ordenHTTP.put("institucionContraparte", ordenValida.getInstitucionContraparte().toString());
                                                        ordenHTTP.put("empresa", ordenValida.getEmpresa().trim());
                                                        ordenHTTP.put("claveRastreo", ordenValida.getClaveRastreo());
                                                        ordenHTTP.put("institucionOperante", ordenValida.getInstitucionOperante());
                                                        ordenHTTP.put("monto", ordenValida.getMonto());
                                                        ordenHTTP.put("tipoPago", ordenValida.getTipoPago());
                                                        ordenHTTP.put("tipoCuentaOrdenante", ordenValida.getTipoCuentaOrdenante());
                                                        ordenHTTP.put("nombreOrdenante", ordenValida.getNombreOrdenante());
                                                        ordenHTTP.put("cuentaOrdenante", ordenValida.getCuentaOrdenante());
                                                        ordenHTTP.put("rfcCurpOrdenante", ordenValida.getRfcCurpOrdenante());
                                                        ordenHTTP.put("tipoCuentaBeneficiario", ordenValida.getTipoCuentaBeneficiario());
                                                        ordenHTTP.put("nombreBeneficiario", ordenValida.getNombreBeneficiario());
                                                        ordenHTTP.put("cuentaBeneficiario", ordenValida.getCuentaBeneficiario().trim().replace(" ", ""));
                                                        ordenHTTP.put("rfcCurpBeneficiario", ordenValida.getRfcCurpBeneficiario());
                                                        ordenHTTP.put("conceptoPago", ordenValida.getConceptoPago());
                                                        ordenHTTP.put("referenciaNumerica", ordenValida.getReferenciaNumerica());
                                                        ordenHTTP.put("nombreParticipanteIndirecto", ordenValida.getNombreParticipanteIndirecto());
                                                        ordenHTTP.put("cuentaParticipanteIndirecto", ordenValida.getCuentaParticipanteIndirecto());
                                                        ordenHTTP.put("rfcParticipanteIndirecto", ordenValida.getRfcParticipanteIndirecto());
                                                        ordenHTTP.put("firma", firmarEnviarOrden(ordenValida));


                                                        //CargoSpei cargoSpei = cargoSpeiService.guardarCargoSpei();
                                                        log.info("Orden preparar a enviar:" + ordenValida);
                                                        String response = httpMethods.enviarOrdenSpei(ordenHTTP.toString());
                                                        STPDispersionResponseVo re= json.fromJson(response, STPDispersionResponseVo.class);
                                                        log.info("::::::Respuesta al enviar orden::::::::" + re);
                                                        //dispersion.setId(response.getResultado().getId());
                                                    } else {
                                                        log.warn("::::::::::::::Error al procesar validacion:::::::::::::::::" + ordenValida.getError());
                                                        dispersion.setId(0);
                                                        dispersion.setError(ordenValida.getError());
                                                    }
                                                } else {
                                                    log.info("::::::::::::::::Banco receptor no existe::::::::::::::" + order.getInstitucionContraparte() + ".......");
                                                    dispersion.setId(0);
                                                    dispersion.setError("Banco receptor no existe:" + order.getInstitucionContraparte());
                                                }
                                            } else {
                                                log.info("......Monto esta por debajo al permitido en el core......");
                                                dispersion.setId(0);
                                                dispersion.setError("Monto esta por debajo al permitido en el core");
                                            }
                                        } else {
                                            log.info("::::::::::::::Se requiere configuracion de monto minimo::::::::::::::");
                                            dispersion.setId(0);
                                            dispersion.setError("Se requiere configuracion de monto minimo");
                                        }
                                    } else {
                                        log.info(".....Monto es mayor al permitido en el core......");
                                        dispersion.setId(0);
                                        dispersion.setError("Monto es mayor al permitido en el core");
                                    }
                                } else {
                                    log.info("::::::::::::Se requiere configuracion de monto maximo::::::::::::");
                                    dispersion.setId(0);
                                    dispersion.setError("Se requiere configuracion de monto maximo");
                                }

                            } else {
                                log.error("::::::::::::::::Registros para clabe no existe o inactivo::::::::::::::");
                                dispersion.setId(0);
                                dispersion.setError("Registros para clabe no existe o inactivo");
                            }
                        } else {
                            dispersion.setId(0);
                            dispersion.setError("El producto configurado no es el mismo a operar");
                            log.error(":::::::::::::::::::::El producto configurado no es el mismo a operar:::::::::::::::::::::::");
                        }
                    } else {
                        dispersion.setId(0);
                        dispersion.setError("No existe configuracion para buscar producto a operar");
                        log.error("::::::::::::::::::No existe configuracion para buscar producto a operar::::::::::::::::::");
                    }
                } else {
                    dispersion.setId(0);
                    dispersion.setError("Opa cliente inactivo");
                    log.error("::::::::::::::::Opa cliente inactivo:::::::::::::::::::");
                }
            } else {
                dispersion.setId(0);
                dispersion.setError("Opa Cliente no encontrado");
                log.error(":::::::::::::Opa Cliente no encontrado::::::::::::::");
            }

        } catch (Exception e) {
            log.info("Error al enviar orden:" + e.getMessage());
        }
        return dispersion;
    }


    private OrdenPagoWS validarFormarOrdenPago(RequestLocalDispersionVo orden) {
        log.info("¡Vamos a validar los datos!");
        OpaDTO opa = util.opa(orden.getOpaCliente());
        OrdenPagoWS ordenEnviada = new OrdenPagoWS();

        try {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String fechaActual = sdf.format(now).replace("/", "");

            //Primero busco el folio que esta intentando hacer la orden SPEI
            AuxiliarPK auxiliarPK = new AuxiliarPK(opa.getIdorigenp(), opa.getIdproducto(), opa.getIdauxiliar());
            Auxiliar auxiliar = auxiliarService.buscarPorId(auxiliarPK);

            if (auxiliar != null) {
                if (auxiliar.getEstatus() == 2) {
                    PersonaPK personaPK = new PersonaPK(auxiliar.getIdorigen(), auxiliar.getIdgrupo(), auxiliar.getIdsocio());
                    Persona persona = personaService.buscarPorId(personaPK);
                    if (persona != null) {
                        System.out.println("si persona");
                        //Vamos a buscar el banco receptor
                        Banco institucionDestino = bancoService.buscarNombre(orden.getInstitucionContraparte().toUpperCase().trim());
                        if (institucionDestino != null) {
                            System.out.println("si banco");
                            //Genero la clave de rastreo
                            Random rnd = new Random();
                            //Busco empresa
                            TablaPK tbpk = new TablaPK("spei_csn", "empresa");
                            Tabla empresa = tablaService.buscarPorId(tbpk);
                            String claveRastreo = empresa.getDato1() + fechaActual + String.valueOf(rnd.nextInt(500000000 - 4 + 2) + 5);
                            System.out.println("si sivisiv");
                            //para csn siempre sera el producto 133
                            ordenEnviada.setInstitucionContraparte(institucionDestino.getIdbanco());
                            ordenEnviada.setEmpresa(empresa.getDato1().trim());
                            ordenEnviada.setClaveRastreo(claveRastreo.trim());
                            ordenEnviada.setMonto(String.valueOf(orden.getMonto()));
                            //Institucion operante es fijo para STP
                            ordenEnviada.setInstitucionOperante(90646);
                            ordenEnviada.setNombreParticipanteIndirecto("CSN COOPERATIVA FINANCIERA");
                            ordenEnviada.setCuentaParticipanteIndirecto("646180132300000004");
                            ordenEnviada.setRfcParticipanteIndirecto("CSN950904LU6");
                            //Tipo pago es fijo (1.-Tercero-Tercero)
                            ordenEnviada.setTipoPago(30);//Cambio de 1 a 30 El 08/04/2024
                            //Estatico porque solo se manejan cuentas clabe
                            ordenEnviada.setTipoCuentaOrdenante(40);
                            ordenEnviada.setNombreOrdenante(valida_caracteres_speciales(persona.getNombre().trim())/*p.getNombre().trim()*/ + " "
                                    + valida_caracteres_speciales(persona.getAppaterno().trim())/*p.getAppaterno().trim()*/ + " "
                                    + valida_caracteres_speciales(persona.getApmaterno().trim()));// + p.getApmaterno().trim());

                            //Busco la clabe interbancaria
                            ClabeInterbancaria clabe = clabeInterbancariaService.buscarPorId(auxiliar.getAuxiliarPK());
                            System.out.println("dmdlfm");
                            if (clabe.isActiva()) {
                                System.out.println("dentro");
                                ordenEnviada.setCuentaOrdenante(clabe.getClabe().trim());
                                ordenEnviada.setRfcCurpOrdenante(persona.getCurp().trim());
                                ordenEnviada.setTipoCuentaBeneficiario(40);
                                orden.setNombreBeneficiario(valida_caracteres_speciales(orden.getNombreBeneficiario().trim()));
                                ordenEnviada.setCuentaBeneficiario(orden.getCuentaBeneficiario().trim().replace(" ", ""));
                                ordenEnviada.setRfcCurpBeneficiario(orden.getRfcCurpBeneficiario().trim());
                                int referenciaNumerica = rnd.nextInt(300000 - 8 + 1) + 7;
                                orden.setConceptoPago(valida_caracteres_speciales(orden.getConceptoPago().trim()));
                                ordenEnviada.setReferenciaNumerica(referenciaNumerica);
                                //lo utilizo para darle la numeracion que la documentacion me pide
                                DecimalFormat df1 = new DecimalFormat("#.00");
                                df1.setMaximumFractionDigits(2);
                                df1.setMaximumIntegerDigits(19);
                                String montoCodificado = "";
                                if (df1.getMaximumIntegerDigits() == 19 && df1.getMaximumFractionDigits() == 2) {
                                    montoCodificado = df1.format(new BigDecimal(orden.getMonto()));
                                }
                                ordenEnviada.setMonto(montoCodificado);
                                ordenEnviada.setError("");
                                ordenEnviada.setCodigo(200);
                            } else {
                                ordenEnviada.setCodigo(0);
                                ordenEnviada.setError("Clabe inactiva");
                                log.error(":::::::::::::::::::Clabe a operar inactiva::::::::::");
                            }
                        } else {
                            ordenEnviada.setCodigo(0);
                            ordenEnviada.setError("Institucion invalida");
                            log.error(":::::::::::::::::::::institucion invalida:::::::::::::::::::");
                        }
                    } else {
                        ordenEnviada.setCodigo(0);
                        ordenEnviada.setError("No existe registro persona");
                        log.error(":::::::::::::::No existe registro de persona:::::::::::::::");
                    }
                } else {
                    ordenEnviada.setCodigo(0);
                    ordenEnviada.setError("Estatus invalido para auxiliar");
                    log.error(":::::::::::::::Estatus auxiliar invalido:::::::::::::::" + auxiliar.getEstatus());
                }
            } else {
                ordenEnviada.setCodigo(0);
                ordenEnviada.setError("No existe folio opa");
                log.error(":::::::::::::::No existe folio opa:::::::::::::::");
            }
        } catch (Exception e) {
            log.error("***********************Error al validar formar orden a enviar*********************:" + e.getMessage());
        }

        return ordenEnviada;
    }

    private STPDispersionResponseVo enviarOrdenHTTP(RequestLocalDispersionVo orden) {
        STPDispersionResponseVo response = new STPDispersionResponseVo();
        try {
            STPOrderRequestVo ordenSTP = new STPOrderRequestVo();
            ordenSTP.setClaveRastreo(generarCadenaAlfanumerica(8));
            ordenSTP.setConceptoPago(orden.getConceptoPago());
            ordenSTP.setTipoCuentaOrdenante(orden.getOpaCliente());
            ordenSTP.setCuentaBeneficiario(orden.getCuentaBeneficiario());
            TablaPK tbPk = new TablaPK(idtabla, "empresa");
            Tabla tabla = tablaService.buscarPorId(tbPk);

            ordenSTP.setEmpresa("");// Solicitarlo a Caja
            ordenSTP.setInstitucionContraparte(orden.getInstitucionContraparte());
            ordenSTP.setInstitucionOperante("");// Solicitarlo a caja
            ordenSTP.setMonto(String.valueOf(orden.getMonto()));
            ordenSTP.setNombreBeneficiario(orden.getNombreBeneficiario());
            ClabeInterbancaria clabe = clabeInterbancariaService.buscarPorClabe(orden.getOpaCliente());
            Auxiliar auxiliar = auxiliarService.buscarPorId(clabe.getAuxPk());
            PersonaPK personaPk = new PersonaPK(auxiliar.getIdorigen(), auxiliar.getIdgrupo(), auxiliar.getIdsocio());
            Persona persona = personaService.buscarPorId(personaPk);
            ordenSTP.setNombreOrdenante(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
            ordenSTP.setReferenciaNumerica(generarCadenaAlfanumerica(10));
            ordenSTP.setRfcCurpBeneficiario(orden.getRfcCurpBeneficiario());
            ordenSTP.setRfcCurpOrdenante(persona.getCurp());
            ordenSTP.setTipoCuentaBeneficiario("40");
            ordenSTP.setTipoCuentaOrdenante("40");
            ordenSTP.setTipoPago("1");
            //ordenSTP.setFirma(firmarEnviarOrden(ordenSTP());

            String peticion = json.toJson(ordenSTP);
            String respuestaOrden = httpMethods.enviarOrdenSpei(peticion);
            log.info("Respuesta STP:" + respuestaOrden);

            Gson json = new Gson();
            response = json.fromJson(respuestaOrden, STPDispersionResponseVo.class);
            log.info("Response STP:" + response);

        } catch (Exception e) {
            log.info("Error al enviar orden SPEI:" + e.getMessage());
        }

        return response;

    }

    public String valida_caracteres_speciales(String cadena) {
        cadena = cadena.toLowerCase();
        for (int i = 0; i < cadena.length(); i++) {
            int ascii = cadena.charAt(i);
            char c = cadena.charAt(i);
            if (cadena.charAt(i) == ' ' || Character.isLetter(c) || Character.isDigit(c)) {

                switch (c) {
                    case 'á':
                        cadena = cadena.replace(String.valueOf(c), "a");
                        break;
                    case 'é':
                        cadena = cadena.replace(String.valueOf(c), "e");
                        break;
                    case 'í':
                        cadena = cadena.replace(String.valueOf(c), "a");
                        break;
                    case 'ó':
                        cadena = cadena.replace(String.valueOf(c), "o");
                        break;
                    case 'ú':
                        cadena = cadena.replace(String.valueOf(c), "u");
                        break;
                    case 'ñ':
                        cadena = cadena.replace(String.valueOf(c), "n");
                        break;
                    //áéíóúñ
                }
            } else {
                cadena = cadena.replace(String.valueOf(c), "");
            }

            //System.out.println("El caracter en la posicion "+i+"es:"+cadena.charAt(i)+" y su valor ascii es:"+ascii);
        }
        //System.out.println("la cadena es:"+cadena.trim());

        return cadena;
    }

    public String firmarEnviarOrden(OrdenPagoWS oPW) {
        StringBuilder sB = new StringBuilder();
        String firma = "";
        try {
            sB.append("||");
            sB.append(oPW.getInstitucionContraparte()).append("|");
            sB.append(oPW.getEmpresa()).append("|");
            sB.append(oPW.getFechaOperacion() == null ? "" : oPW.getFechaOperacion()).append("|");
            sB.append(oPW.getFolioOrigen() == null ? "" : oPW.getFolioOrigen()).append("|");
            sB.append(oPW.getClaveRastreo() == null ? "" : oPW.getClaveRastreo()).append("|");
            sB.append(oPW.getInstitucionOperante() == null ? "" : oPW.getInstitucionOperante()).append("|");
            sB.append(oPW.getMonto() == null ? "" : oPW.getMonto()).append("|");
            sB.append(oPW.getTipoPago() == null ? "" : oPW.getTipoPago()).append("|");
            sB.append(oPW.getTipoCuentaOrdenante() == null ? "" : oPW.getTipoCuentaOrdenante()).append("|");
            sB.append(oPW.getNombreOrdenante() == null ? "" : oPW.getNombreOrdenante()).append("|");
            sB.append(oPW.getCuentaOrdenante() == null ? "" : oPW.getCuentaOrdenante()).append("|");
            sB.append(oPW.getRfcCurpOrdenante() == null ? "" : oPW.getRfcCurpOrdenante()).append("|");
            sB.append(oPW.getTipoCuentaBeneficiario() == null ? "" : oPW.getTipoCuentaBeneficiario()).append("|");
            sB.append(oPW.getNombreBeneficiario() == null ? "" : oPW.getNombreBeneficiario()).append("|");
            sB.append(oPW.getCuentaBeneficiario() == null ? "" : oPW.getCuentaBeneficiario()).append("|");
            sB.append(oPW.getRfcCurpBeneficiario() == null ? "" : oPW.getRfcCurpBeneficiario()).append("|");
            sB.append(oPW.getEmailBeneficiario() == null ? "" : oPW.getEmailBeneficiario()).append("|");
            sB.append(oPW.getTipoCuentaBeneficiario2() == null ? "" : oPW.getTipoCuentaBeneficiario2()).append("|");
            sB.append(oPW.getNombreBeneficiario2() == null ? "" : oPW.getNombreBeneficiario2()).append("|");
            sB.append(oPW.getCuentaBeneficiario2() == null ? "" : oPW.getCuentaBeneficiario2()).append("|");
            sB.append(oPW.getRfcCurpBeneficiario2() == null ? "" : oPW.getRfcCurpBeneficiario2()).append("|");
            sB.append(oPW.getConceptoPago() == null ? "" : oPW.getConceptoPago()).append("|");
            sB.append(oPW.getConceptoPago2() == null ? "" : oPW.getConceptoPago2()).append("|");
            sB.append(oPW.getClaveCatUsuario1() == null ? "" : oPW.getClaveCatUsuario1()).append("|");
            sB.append(oPW.getClaveCatUsuario2() == null ? "" : oPW.getClaveCatUsuario2()).append("|");
            sB.append(oPW.getClavePago() == null ? "" : oPW.getClavePago()).append("|");
            sB.append(oPW.getReferenciaCobranza() == null ? "" : oPW.getReferenciaCobranza()).append("|");
            sB.append(oPW.getReferenciaNumerica() == null ? "" : oPW.getReferenciaNumerica()).append("|");
            sB.append(oPW.getTipoOperacion() == null ? "" : oPW.getTipoOperacion()).append("|");
            sB.append(oPW.getTopologia() == null ? "" : oPW.getTopologia()).append("|");
            sB.append(oPW.getUsuario() == null ? "" : oPW.getUsuario()).append("|");
            sB.append(oPW.getMedioEntrega() == null ? "" : oPW.getMedioEntrega()).append("|");
            sB.append(oPW.getPrioridad() == null ? "" : oPW.getPrioridad()).append("|");
            sB.append(oPW.getIva() == null ? "" : oPW.getIva()).append("|");
            sB.append(oPW.getNombreParticipanteIndirecto() == null ? "" : oPW.getNombreParticipanteIndirecto()).append("|");
            sB.append(oPW.getCuentaParticipanteIndirecto() == null ? "" : oPW.getCuentaParticipanteIndirecto()).append("|");
            sB.append(oPW.getRfcParticipanteIndirecto() == null ? "" : oPW.getRfcParticipanteIndirecto()).append("||");

            String cadena = sB.toString();

            System.out.println("Cadena formada enviar orden:" + cadena);

            String firmaSincodificar = sign(cadena);
            firma = firmaSincodificar;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error al generar firma:" + e.getMessage());
        }
        System.out.println("Firma:" + firma);
        return firma;
    }

    // Consigo mi firma
    public String sign(String cadena) throws Exception {
        String firmaCod;
        // Direccion de mi keystore local
        String fileName = ruta() + "fenoreste.jks";
        String password = "fenoreste2024";
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
