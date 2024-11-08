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
import fenoreste.spei.consumo.ConsumoCsnTDD;
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
    private ICargoSpeiService cargoSpeiService;
    @Autowired
    private IOrigenService origenService;
    @Autowired
    private IFolioTarjetaService folioTarjetaService;
    @Autowired
    private ITarjetaService tarjetaService;
    @Autowired
    private ISpeiTemporalService speiTemporalService;



    @Autowired
    private Util util;

    @Autowired
    private HttpMethods httpMethods;

    private String idtabla = "spei_salida";
    TablaPK tb_pk = null;
    Tabla tb_usuario = null;
    Usuario user_ins = null;
    Origen matriz = null;
    ResponseLocalDispersionVo valiResponse = new ResponseLocalDispersionVo();
    SpeiTemporal temporal = new SpeiTemporal();

    Gson json = new Gson();

    @Autowired
    private ConsumoCsnTDD consumoCsnTDD;

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
                    TablaPK tbpk = new TablaPK(idtabla, "producto_spei");
                    Tabla tabla = tablaService.buscarPorId(tbpk);
                    if (tabla != null) {
                        if (Integer.parseInt(tabla.getDato1()) == opa.getIdproducto()) {
                            ClabeInterbancaria clabe = clabeInterbancariaService.buscarPorId(a.getAuxiliarPK());
                            if (clabe != null) {
                                //Vamos a validar reglas
                                dispersion = validaReglasCsn(a, clabe.getClabe(), order, 0);
                                if (dispersion.getId() == 200) {
                                    //Guardamos la operacion de salida
                                    CargoSpei speiSalida = new CargoSpei();
                                    speiSalida.setCuentabeneficiario(clabe.getClabe());
                                    speiSalida.setMonto(order.getMonto());
                                    speiSalida.setInstitucioncontraparte(order.getInstitucionContraparte());
                                    speiSalida.setNombrebeneficiario(order.getNombreBeneficiario());
                                    speiSalida.setRfccurpbeneficiario(order.getRfcCurpBeneficiario());
                                    speiSalida.setConceptopago(order.getConceptoPago());
                                    speiSalida.setIdorden(0);
                                    speiSalida.setEstatus("No enviado....");
                                    speiSalida.setFechaentrada(new Date());
                                    cargoSpeiService.guardarCargoSpei(speiSalida);

                                    //Primero buscamos que el producto que va a dispersar tenga saldo suficiente

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
                                                            STPDispersionResponseVo re = json.fromJson(response, STPDispersionResponseVo.class);
                                                            STPResultadoVo rt = new STPResultadoVo();
                                                            rt.setId(267778);
                                                            re.setResultado(rt);
                                                            log.info("::::::Respuesta al enviar orden::::::::" + re);
                                                            speiSalida.setClaverastreo(ordenValida.getClaveRastreo());
                                                            speiSalida.setNombreordenante(ordenValida.getNombreOrdenante());
                                                            speiSalida.setIdorden(re.getResultado().getId());
                                                            speiSalida.setNombrebeneficiario(ordenValida.getNombreBeneficiario());
                                                            speiSalida.setTipocuentaordenante(ordenValida.getTipoCuentaOrdenante());
                                                            speiSalida.setReferencianumerica(ordenValida.getReferenciaNumerica());
                                                            speiSalida.setEmpresa(ordenValida.getEmpresa());
                                                            speiSalida.setInstitucionoperante(ordenValida.getInstitucionOperante());
                                                            speiSalida.setTipocuentaordenante(ordenValida.getTipoCuentaOrdenante());
                                                            speiSalida.setCuentaordenante(ordenValida.getCuentaOrdenante());
                                                            speiSalida.setRfccurpordenante(ordenValida.getRfcCurpOrdenante());
                                                            speiSalida.setRfccurpbeneficiario(ordenValida.getRfcCurpBeneficiario());
                                                            speiSalida.setNombrebeneficiario(ordenValida.getNombreBeneficiario());
                                                            speiSalida.setConceptopago(ordenValida.getConceptoPago());


                                                            if (String.valueOf(re.getResultado().getId()).length() > 3) {
                                                                speiSalida.setClaverastreo(ordenValida.getClaveRastreo());
                                                                speiSalida.setNombreordenante(ordenValida.getNombreOrdenante());
                                                                speiSalida.setEstatus("Enviado.....");
                                                                speiSalida.setIdorden(re.getResultado().getId());
                                                                speiSalida.setAplicado(true);
                                                                speiSalida.setFechaejecucion(new Date());
                                                                speiSalida.setNombrebeneficiario(ordenValida.getNombreBeneficiario());

                                                                dispersion.setId(rt.getId());
                                                                dispersion.setError("OK");
                                                                //vamos a persistir en el core
                                                                realizarTransferencia(a,ordenValida.getReferenciaNumerica(),order,1);

                                                            } else {
                                                                dispersion.setId(0);
                                                                dispersion.setError(re.getResultado().getDescripcionError());
                                                                speiSalida.setMensaje_core(re.getResultado().getDescripcionError());
                                                            }
                                                            cargoSpeiService.guardarCargoSpei(speiSalida);
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
                                }else{
                                    dispersion.setId(0);
                                    dispersion.setError(dispersion.getError());
                                    log.error("Error al realizar la validacion:"+dispersion.getError());
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


    private ResponseLocalDispersionVo validaReglasCsn(Auxiliar opa, String clabe, RequestLocalDispersionVo orden, Integer tipoOperacion) {
        log.info(":::::::::::::::::::::::Validando regla para CSN::::::::::::::::::::");
        ResponseLocalDispersionVo validacion = new ResponseLocalDispersionVo();
        try {
            matriz = origenService.buscarMatriz();
            PersonaPK personaPk = new PersonaPK(opa.getIdorigen(), opa.getIdgrupo(), opa.getIdsocio());
            Persona persona = personaService.buscarPorId(personaPk);
            if (persona.getPk().getIdgrupo() == 10) {
                // Buscamos la tabla donde esta la url
                TablaPK tbpk = null;
                Tabla tabla = null;
                // Vamos a buscar si esta la tdd
                FolioTarjeta folioTarjeta = null;
                Tarjeta tarjeta = null;
                boolean bandera = false;
                switch (tipoOperacion) {
                    case 0:// Validacion reglas generales
                        tbpk = new TablaPK("bankingly_banca_movil", "producto_tdd");
                        tabla = tablaService.buscarPorId(tbpk);
                        //Validamos saldo en la cuenta
                        Double saldo = 0.0;
                        boolean banderaSaldo = false;
                        if (opa.getAuxiliarPK().getIdproducto() == Integer.parseInt(tabla.getDato1())) {
                            //Aqui buscamos el saldo de la tarjeta
                            log.info("::::::::::::::Vamos a buscar servicio activo TDD::::::::::::::");
                            tbpk = new TablaPK("spei_entrada", "activar_uso_tdd");
                            tabla = tablaService.buscarPorId(tbpk);
                            log.info("::::::::::::::::::::Servicio TDD:" + tabla.getDato1() + ":::::::::::::::::");
                            if (tabla.getDato1().equals("1")) {
                                folioTarjeta = folioTarjetaService.buscarPorId(opa.getAuxiliarPK());
                                String json = consumoCsnTDD.obtenerSaldo(tabla.getDato2(), folioTarjeta.getIdtarjeta());
                                JSONObject obj = new JSONObject(json);
                                saldo = Double.parseDouble(obj.getString("availableAmount"));
                                //vamos a buscar la tabla de comisiones para restarle lo que tiene que pagar
                                tbpk = new TablaPK(idtabla, "cuenta_comision");
                                tabla = tablaService.buscarPorId(tbpk);
                                if (tabla.getDato3().equals("1")) {
                                    if (saldo >= (orden.getMonto() + Double.parseDouble(tabla.getDato2()) + Double.parseDouble(tabla.getDato2()) * 0.16)) {
                                        banderaSaldo = true;
                                    }
                                }
                            }
                        } else {
                            saldo = opa.getSaldo().doubleValue();
                            tbpk = new TablaPK(idtabla, "cuenta_comisiones");
                            tabla = tablaService.buscarPorId(tbpk);
                            if (tabla.getDato3().equals("1")) {
                                if (saldo >= (orden.getMonto() + Double.parseDouble(tabla.getDato2()) + Double.parseDouble(tabla.getDato2()) * 0.16)) {
                                    banderaSaldo = true;
                                }
                            }
                        }

                        if (banderaSaldo) {
                            // Buscamos minimo y maximo a operar
                            tbpk = new TablaPK(idtabla, "monto_minimo");
                            Tabla tb_minimo = tablaService.buscarPorId(tbpk);
                            tbpk.setIdElemento("monto_maximo");
                            Tabla tb_maximo = tablaService.buscarPorId(tbpk);
                            Double monto_minimo = new Double(tb_minimo.getDato1());
                            Double monto_maximo = new Double(tb_maximo.getDato1());

                            if (orden.getMonto() >= monto_minimo) {
                                if (orden.getMonto() <= monto_maximo) {
                                    // Vamos a validar estatus de la clabe interbancaria
                                    ClabeInterbancaria cbeInterbancaria = clabeInterbancariaService.buscarPorClabe(clabe);
                                    if (cbeInterbancaria != null) {
                                        if (!cbeInterbancaria.isBloqueada()) {
                                            if (cbeInterbancaria.isActiva()) {
                                                // Vamos a buscar una TDD
                                                folioTarjeta = folioTarjetaService.buscarPorId(opa.getAuxiliarPK());
                                                if (folioTarjeta != null) {
                                                    // Buscamos registro para tarjeta
                                                    tarjeta = tarjetaService.buscarPorId(folioTarjeta.getIdtarjeta());
                                                    if (tarjeta != null) {
                                                        // Validamos fecha de vencimiendo
                                                        tbpk= new TablaPK("valor_udi",matriz.getFechatrabajo().toString().substring(0,7).replace("\\/","").replace("-",""));
                                                        tabla = tablaService.buscarPorId(tbpk);
                                                        String valor_udi = tabla.getDato1();

                                                        tbpk = new TablaPK(idtabla,"max_udis_transferencia");
                                                        tabla = tablaService.buscarPorId(tbpk);
                                                        if (orden.getMonto() <= (Double.parseDouble(valor_udi) * Double.parseDouble(tabla.getDato1()))) {
                                                               validacion.setId(200);
                                                        }else{
                                                            validacion.setId(0);
                                                            validacion.setError("UDIS traspasa al permitido por operacion");
                                                            log.info("::::::::::::::::UDIS traspasa al permitido por operacion:::::::::::::::::::");
                                                        }

                                                       /* Double acumulado = abonoSpeiService.montoDiario(fechaOperacion, clabe);

                                                        if ((acumulado + monto) < new Double(tb_monto_maximo_diario.getDato1())) {
                                                            response.setMensaje("OK");
                                                            response.setId(999);
                                                            response.setCodigo(200);
                                                        } else {
                                                            log.info(
                                                                    "..........el monto operado hoy supera el permitido en el core..........");
                                                            response.setMensaje(
                                                                    "El monto operado hoy supera el permitido en el core");
                                                            response.setId(25);
                                                        }*/
                                                        /*
                                                         * } else {
                                                         * log.info("..............Tarjeta de debito esta vencida...........");
                                                         * response.setMensaje("Tarjeta de Debito esta vencida");
                                                         * response.setId(15); }
                                                         */
                                                    } else {
                                                        log.info(".............Tarjeta de Debito no encontrada..............");
                                                        validacion.setError("Tarjeta de Debito no encontrada");
                                                        validacion.setId(15);

                                                    }
                                                } else {
                                                    log.info("............Sin registros para TDD...........");
                                                    validacion.setError("Sin registros para TDD");
                                                    validacion.setId(14);
                                                }
                                            } else {
                                                log.info("............Clabe Inactiva...........");
                                                validacion.setError("La Clabe esta inactiva");
                                                validacion.setId(14);
                                            }
                                        } else {
                                            log.info("............Clabe bloqueada...........");
                                            validacion.setError("Clabe Bloqueada");
                                            validacion.setId(14);
                                        }

                                    } else {
                                        log.info("............Clabe interbancaria vencida...........");
                                        validacion.setError("Clabe interbancaria expirada");
                                        validacion.setId(14);
                                    }
                                } else {
                                    log.info(".........Monto es mayor al permitido en el core........");
                                    validacion.setError("Monto es mayor al permitido en el core");
                                    validacion.setId(25);
                                }
                            } else {
                                log.info(".........Monto es menor al permitido en el core........");
                                validacion.setError("Monto es menor al permitido en el core");
                                validacion.setId(25);
                            }
                        } else {
                            log.info(".........Saldo es menor al monto a operar........");
                            validacion.setError("Monto es menor al operar en el core");
                            validacion.setId(25);
                        }

                        break;
                    case 1: // deposito a TDD
                        tbpk = new TablaPK(idtabla, "activar_uso_tdd");
                        tabla = tablaService.buscarPorId(tbpk);
                        if (tabla.getDato1().equals("1")) {
                            folioTarjeta = folioTarjetaService.buscarPorId(opa.getAuxiliarPK());
                            tarjeta = tarjetaService.buscarPorId(folioTarjeta.getIdtarjeta());
                            bandera = consumoCsnTDD.depositarSaldo(tabla.getDato2(), tarjeta.getIdtarjeta(), orden.getMonto());
                            if (bandera) {
                                validacion.setId(999);
                                validacion.setError("OK");
                                log.info(":::::::::Csn Dposito TDD exitoso:" + orden.getMonto() + ":::::::");
                            } else {
                                validacion.setId(23);
                                validacion.setError("Error al realizar deposito a TDD");
                                log.info("..............Error al realizar deposito a TDD..........");
                            }
                        } else {
                            validacion.setId(23);
                            validacion.setError("Asegurate de activar servicios de Tarjeta de Debito");
                            log.info("Asegurate de activar los servicios de Tarjeta de Debito");
                        }
                        break;
                    case 2: // Retiro TDD
                        tbpk = new TablaPK(idtabla, "activar_uso_tdd");
                        tabla = tablaService.buscarPorId(tbpk);
                        if (tabla.getDato1().equals("1")) {
                            folioTarjeta = folioTarjetaService.buscarPorId(opa.getAuxiliarPK());
                            tarjeta = tarjetaService.buscarPorId(folioTarjeta.getIdtarjeta());
                            bandera = consumoCsnTDD.retirarSaldo(tabla.getDato2(), tarjeta.getIdtarjeta(), orden.getMonto());
                            if (bandera) {
                                validacion.setId(999);
                                validacion.setError("OK");
                                log.info(":::::::::Csn Retiro TDD exitoso:" + orden.getMonto() + ":::::::");
                            } else {
                                validacion.setId(23);
                                validacion.setError("Error al realizar retiro a TDD");
                                log.info("..............Error al realizar retiro a TDD..........");
                            }
                        } else {
                            validacion.setId(23);
                            validacion.setError("Asegurate de activar servicios de Tarjeta de Debito");
                            log.info("Asegurate de activar los servicios de Tarjeta de Debito");
                        }
                        break;

                }

            } else {
                log.info(".........Grupo para socio no permitido en el core........");
                validacion.setError("Socio perteneciente a grupo no permitido en el core");
                validacion.setId(19);
            }

        } catch (Exception e) {
            log.info("....Error al validar reglas CSN..." + validacion.getError());
            validacion.setError("Error al validar reglas CSN");
        }
        return validacion;
    }

    private int realizarTransferencia(Auxiliar opa,Integer referencia,RequestLocalDispersionVo orden, int tipoop) {
        int aplicados = 0;
        try {

            String sesion = funcionesSaiService.session();
            matriz = origenService.buscarMatriz();
            // Buscamos tabla para idcuenta
            tb_pk = new TablaPK(idtabla, "cuenta_contable");
            Tabla tb_cuenta_contable = tablaService.buscarPorId(tb_pk);
            tb_pk = new TablaPK(idtabla, "usuario");
            tb_usuario = tablaService.buscarPorId(tb_pk);


            // Vamos a registrar movimiento a producto abono(Abono)
            System.out.println("Guardando cargo a opa");
            temporal.setIdorigen(opa.getIdorigen());
            temporal.setIdgrupo(opa.getIdgrupo());
            temporal.setIdsocio(opa.getIdsocio());
            temporal.setIdorigenp(opa.getAuxiliarPK().getIdorigenp());
            temporal.setIdproducto(opa.getAuxiliarPK().getIdproducto());
            temporal.setIdauxiliar(opa.getAuxiliarPK().getIdauxiliar());
            temporal.setReferencia(String.valueOf(referencia));
            if (tipoop == 1) {
                temporal.setConcepto_mov("SPEI ENTRADA");
                temporal.setEsentrada(false);
            } else if (tipoop == 2) {
                temporal.setConcepto_mov("SPEI SALIDA: Retroceso por timeout");
                temporal.setEsentrada(true);
            } else if (tipoop == 3) {
                log.info("Error poliza de retroceso");
                temporal.setConcepto_mov("SPEI ENTRADA: Retroceso por falla general");
                temporal.setEsentrada(false);
            }
            temporal.setAcapital(orden.getMonto());
            temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
            temporal.setSesion(funcionesSaiService.session());
            //String sai = funcionesSaiService.sai_auxiliar(new AuxiliarPK(temporal.getIdorigenp(),temporal.getIdproducto(), temporal.getIdauxiliar()));
            //temporal.setSai_aux(sai);
            temporal.setMov(1);
            temporal.setTipopoliza(3);
            speiTemporalService.guardar(temporal);

            // Vamos a registrar el movimiento a cuentaContable(Cargo) para el balance
            System.out.println("Guardando abono a cuenta");
            temporal = new SpeiTemporal();
            System.out.println("i-3");
            System.out.println("Cuenta contable:"+tb_cuenta_contable);
            temporal.setIdcuenta(tb_cuenta_contable.getDato1());
            System.out.println("i0");
            temporal.setIdorigen(opa.getIdorigen());
            System.out.println("i1");
            temporal.setIdgrupo(opa.getIdgrupo());
            System.out.println("i2");
            temporal.setIdsocio(opa.getIdsocio());
            System.out.println("sigue");
            if (tipoop == 1) {
                temporal.setConcepto_mov("SPEI SALIDA");
                temporal.setEsentrada(true);
            } else if (tipoop == 2) {
                temporal.setConcepto_mov("SPEI SALIDA: Retroceso por timeout");
                temporal.setEsentrada(false);
            } else if (tipoop == 3) {
                temporal.setConcepto_mov("SPEI ENTRADA: Retroceso por falla general");
                temporal.setEsentrada(true);
            }
            temporal.setAcapital(orden.getMonto());
            temporal.setReferencia(String.valueOf(referencia));
            temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
            temporal.setSesion(funcionesSaiService.session());
            temporal.setMov(2);
            temporal.setTipopoliza(3);
            speiTemporalService.guardar(temporal);

            // Vamos a abonar a TDD si el cliente es CSN
            if (matriz.getIdorigen() == 30200) {
                if (tipoop == 1) {
                    log.info("Vamos a retirar a la TDD Alestra");
                    valiResponse = validaReglasCsn(opa,"", orden,2);
                } else {
                    log.info("Vamos a deositar de la TDD Alestra");
                    valiResponse = validaReglasCsn(opa, "",orden, 1);
                }

            } else {
                valiResponse.setId(999);
            }


            if (valiResponse.getId() == 999) {
                // vamos a generar poliza(cargo cuenta spei y abono tdd)
                aplicados = funcionesSaiService.aplica_movs(Integer.parseInt(tb_usuario.getDato1()), temporal.getSesion(), temporal.getTipopoliza(), temporal.getReferencia());
                log.info("total aplicados transferencia spei:" + aplicados);
                if (aplicados <= 0) {
                    //Si falla lo aplicado y el origen es CSN retiramos nuevamente de TDD
                    if (matriz.getIdorigen() == 30200) {
                        valiResponse = validaReglasCsn(opa, "", orden, 2);
                    }
                    aplicados = 0;
                }

            }

            //speiTemporalService.eliminar(sesion, String.valueOf(orden.getReferenciaNumerica()));

        } catch (Exception e) {
            log.error("Error al realizar la transferencia spei:" + e.getMessage());
        }


        return aplicados;

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
