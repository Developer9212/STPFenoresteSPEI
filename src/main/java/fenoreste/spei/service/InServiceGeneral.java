package fenoreste.spei.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import fenoreste.spei.colaProcesor.TransferenciaQueueProcessor;
import fenoreste.spei.consumo.SMSCsn;
import fenoreste.spei.entity.*;
import fenoreste.spei.util.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import fenoreste.spei.consumo.ConsumoCsnTDD;
import fenoreste.spei.modelos.ConcPeticionVo;
import fenoreste.spei.modelos.ConcResultadoVo;
import fenoreste.spei.modelos.ConsultaSaldoPet;
import fenoreste.spei.modelos.SaldoResultadoVo;
import fenoreste.spei.modelos.request;
import fenoreste.spei.modelos.response;
import fenoreste.spei.stp.HttpMethods;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

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

    // Commit
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

    @Autowired
    private ITransferenciaCursoService transferenciaCursoService;

    @Autowired
    IRegistroTemporalService registroTemporalService;

    @Autowired
    private PingService pingService;

    @Autowired
    private TransferenciaQueueProcessor queueProcessor;;


    Gson json = new Gson();

    String idtabla = "spei_entrada";
    int r = 0;

    TablaPK tb_pk = null;
    Tabla tb_usuario = null;
    Origen matriz = null;
    response valiResponse = new response();
    SpeiTemporal temporal = new SpeiTemporal();

    public response sendAbono(request in) throws InterruptedException, ExecutionException, TimeoutException {

        /*Variables para determinar el tiempo para iniciar la operacion*/
        /*=================================================================================*/
        long startTime = System.currentTimeMillis();
        Instant instant1 = Instant.ofEpochMilli(startTime);
        // Convertir el Instant a LocalDateTime usando la zona horaria del sistema
        LocalDateTime timeStart = LocalDateTime.ofInstant(instant1, ZoneId.systemDefault());
        /*=================================================================================*/

        response resp = new response();
        resp.setMensaje("devolver");
        log.info(".......Peticion sendAbono:" + json.toJson(in));
        resp.setCodigo(400);
        // Vamos a registrar la operacion
        AbonoSpeiPK abonoSpeiPK = new AbonoSpeiPK(in.getId(), in.getClaveRastreo());
        AbonoSpei operacion = new AbonoSpei();
        operacion.setAbonoSpeiPK(abonoSpeiPK);
        operacion.setFechaOperacion(in.getFechaOperacion());// in.getFechaOperacion());
        operacion.setInstitucionOrdenante(in.getInstitucionOrdenante());
        operacion.setInstitucionBeneficiaria(in.getInstitucionBeneficiaria());
        //operacion.setClaveRastreo(in.getClaveRastreo());
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
        operacion.setTsliquidacion(in.getTsLiquidacion());
        operacion.setResponsecode(57);

        operacion.setAplicado(false);

        AbonoSpei abono = abonoSpeiService.buscarPorId(abonoSpeiPK);
        boolean operar = false;

        log.info("Se guardo la operacion");
        int transferenciaSpei = 0;
        if (abono != null) {
            //if(!abono.isRetardo() && !abono.isAplicado() && !abono.isStp_ok()){
            log.info("::::::::::::::Se encuentra ya un registro con la claves:" + abonoSpeiPK + "::::::: nos aseguraremos que es la misma operacion en curso");
            if (Objects.equals(abono.getReferenciaNumerica(), in.getReferenciaNumerica()) && Objects.equals(abono.getMonto(), in.getMonto()) && Objects.equals(abono.getAbonoSpeiPK().getId(), in.getId()) && !abono.isStp_ok()) {
                log.info("Abono:" + abono);
                if (abono.isAplicado() && abono.isStp_ok()) {
                    log.info(":::::::::::::::Es la misma pero ya fue aplicada,nada por hacer::::::::::::::::::");
                } else {
                    r = r + 1;
                    log.info(":::::::::::Esto es un reintento para la operacion con llave:" + abono.getAbonoSpeiPK() + "\n nreintentos:" + r + "::::");

                    //Como es un reintento verificamos si se aplico con retardo y si sucedio error en el core intentamos realizarla nuevamente
                    if (abono.isRetardo()) {
                        log.info(":::::::::::::::Se encontro la operacion aplicada con retardo::::::::::::");
                        abono.setStp_ok(true);
                        abonoSpeiService.guardar(abono);
                        resp.setMensaje("confirmar");
                        resp.setCodigo(200);
                    } else {
                        if (!abono.getMensaje_core().replace(" ", "").trim().equalsIgnoreCase("RECHAZADOPORTIMEOUT")) {
                            operar = true;
                        } else {
                            log.info(":::::::::La operacion ya esta aplicada con rechazo por timeout:::::::::::::::");
                        }
                    }
                }
            } else {
                log.info(":::::::::::Operacion con mismas llaves ya esta registrada(aplicada o en devolucion:::::");
                resp.setId(1);
            }
            //}
        } else {
            abonoSpeiService.guardar(operacion);
            operar = true;
        }

        if (operar) {
            // validamos el horario de actividad
            if (funcionesSaiService.horario_actividad()) {
                // Obtenemos origen Matriz
                matriz = origenesService.buscarMatriz();
                // Obtenemos el estatus de origen al que pertenece el usuario
                tb_pk = new TablaPK(idtabla, "usuario");
                tb_usuario = tablasService.buscarPorId(tb_pk);
                Usuario user_in = usuarioService.buscar(Integer.parseInt(tb_usuario.getDato1()));
                Origen origen_usuario = origenesService.buscarPorId(new Integer(user_in.getIdorigen()));
                if (origen_usuario.isEstatus()) {
                    String validacion = "";
                    switch (in.getTipoCuentaBeneficiario()) {
                        case 40:
                            ClabeInterbancaria clabe_registro = clabeInterbancariaService.buscarPorClabe(in.getCuentaBeneficiario().trim());
                            if (clabe_registro != null) {
                                AuxiliarPK a_pk = new AuxiliarPK(clabe_registro.getAuxPk().getIdorigenp(),
                                        clabe_registro.getAuxPk().getIdproducto(),
                                        clabe_registro.getAuxPk().getIdauxiliar());
                                Auxiliar a = auxiliarService.buscarPorId(a_pk);
                                if (matriz.getIdorigen() == 30200) {// CSN
                                    if (a.getEstatus() == 2) {
                                        valiResponse = validaReglasCsn(a, in.getCuentaBeneficiario(), in.getMonto(), in.getFechaOperacion(), 0);
                                    }
                                } else if (matriz.getIdorigen() == 30300) {// Mitras
                                    valiResponse = validaReglasMitras(a, in.getMonto(), in.getFechaOperacion(), operacion.getCuentaBeneficiario());
                                } else if (matriz.getIdorigen() == 30500) {
                                    valiResponse = validaReglasFama(a_pk, in.getMonto(), in.getFechaOperacion(), in.getCuentaBeneficiario());
                                } else {
                                    valiResponse.setCodigo(999);
                                }


                                log.info("................valid response..............." + valiResponse.getId());
                                if (valiResponse.getId() == 999) {
                                    boolean ok_comision = true;
                                    //Realizamos la transferencia abono(clabe-opa) cargo a cuenta spei

                                   // transferenciaSpei = realizarTransferencia(a, in, 1, 1);
                                    CompletableFuture<Integer> future = queueProcessor.enqueue(a, in, 1, 1);
                                    int resultado = future.get();//20,TimeUnit.SECONDS);
                                    log.info("::::::::::::::::::::::::Resultado operacion:::::::::::::"+resultado);
                                    transferenciaSpei = resultado;

                                    if (transferenciaSpei > 0) {
                                        //Vamos a depositar la comision
                                        // Buscamos tabla para comision
                                        TablaPK tb_pk_comision = new TablaPK(idtabla, "monto_comision");
                                        Tabla tb_comision = tablasService.buscarPorId(tb_pk_comision);
                                        if (Double.parseDouble(tb_comision.getDato1()) > 0) {
                                            int transferenciaSpeiComision = realizarTransferenciaComision(a, in, 1);
                                            //Si falla el proceso de operacion comision retrocedemos

                                            if (transferenciaSpeiComision <= 0) {
                                                ok_comision = false;
                                            }
                                        }
                                        if (ok_comision) {
                                            if (in.getMonto() == 11) {
                                                Thread.sleep(15000); //(19100),30000 para el retardo
                                            }


                                            long endTime = System.currentTimeMillis(); //fin tiempo correr operacion
                                            Instant instant2 = Instant.ofEpochMilli(endTime);
                                            // Convertir el Instant a LocalDateTime usando la zona horaria del sistema
                                            LocalDateTime timeEnd = LocalDateTime.ofInstant(instant2, ZoneId.systemDefault());
                                            //ccc
                                            Duration duration = Duration.between(timeStart, timeEnd);
                                            // Obtiene los segundos de la duración
                                            long segundos = duration.getSeconds();
                                            log.info(":::::::Total Segundos en finalizar operacion:::::::" + segundos);
                                            boolean exit = false;
                                            if (valiResponse.getId() == 999) {
                                                if (segundos < 15) {
                                                    resp.setMensaje("confirmar");
                                                    operacion.setAplicado(true);
                                                    operacion.setStp_ok(true);//Modificado el 22/01/2025
                                                    operacion.setFechaProcesada(new Date());
                                                    operacion.setResponsecode(000);
                                                    operacion.setMensaje_core("Terminado con exito");
                                                    resp.setCodigo(200);
                                                    abonoSpeiService.guardar(operacion);
                                                    exit = true;
                                                } else if (segundos >= 15 && segundos <= 20) { // Si tarda más de 40 segundos (40000 ms)
                                                    log.info("::::::::::::::Aplicado con retardo:::::::::::::::");
                                                    operacion.setRetardo(true);
                                                    operacion.setAplicado(true);
                                                    operacion.setMensaje_core("Aplicado con retardo...");
                                                    operacion.setFechaProcesada(new Date());
                                                    resp.setMensaje("confirmar");
                                                    resp.setCodigo(200);
                                                    operacion.setResponsecode(000);
                                                    abonoSpeiService.guardar(operacion);
                                                    exit = true;
                                                } else {
                                                    operacion.setRetardo(false);
                                                    operacion.setAplicado(false);
                                                    operacion.setMensaje_core("Rechazado por timeout");
                                                    resp.setId(20);
                                                    resp.setMensaje("devolver");
                                                    int operacionesEfectuada = realizarTransferencia(a, in, 2, 0);

                                                    if (operacionesEfectuada > 0) {
                                                        if (Double.parseDouble(tb_comision.getDato1()) > 0) {
                                                            realizarTransferenciaComision(a, in, 2);
                                                        }
                                                        abonoSpeiService.guardar(operacion);

                                                        log.info("::::::::::::::Poliza retroceso generada con exito::::::::::::::");
                                                    }
                                                }

                                                if (exit) {
                                                    //Vamos a enviar SMS CSN
                                                    if (matriz.getIdorigen() == 30200) {
                                                        validaReglasCsn(a, null, in.getMonto(), null, 3);
                                                    }
                                                }
                                                log.info("Termino operacion");

                                            }
                                        } else {
                                            //al operar la comision
                                            //funcionesSaiService.eliminaTemporal(1111,sesion);
                                            //speiTemporalService.eliminar(sesion,in.getReferenciaNumerica());
                                            realizarTransferencia(a, in, 3, 0);//Poliza retroceso
                                            operacion.setMensaje_core("Falla al procesar en SAICoop(Comision)");
                                            operacion.setResponsecode(21);
                                            abonoSpeiService.guardar(operacion);
                                            resp.setId(21);
                                            resp.setMensaje("devolver");
                                        }

                                    } else {
                                        // bandera =
                                        // consumoCsnTDD.retirarSaldo(tb_url_tdd.getDato2(),tarjeta.getIdtarjeta(),in.getMonto());
                                        log.info("::::::::::::::::::::::::::::::::::::::::::::::_________aplicado:" + transferenciaSpei);
                                        if (transferenciaSpei == -1) {
                                            log.info("Entrooooooooo");
                                            operacion.setMensaje_core("Error por registro duplicado en temporal");
                                            operacion.setResponsecode(26);
                                            abonoSpeiService.guardar(operacion);
                                            resp.setId(21);
                                            resp.setMensaje("devolver");
                                        } else {
                                            operacion.setMensaje_core("Falla al procesar en SAICoop");
                                            operacion.setResponsecode(21);
                                            abonoSpeiService.guardar(operacion);
                                            resp.setId(21);
                                            resp.setMensaje("devolver");
                                        }

                                    }
                                } else {
                                    operacion.setMensaje_core(resp.getMensaje());
                                    operacion.setResponsecode(valiResponse.getId());
                                    abonoSpeiService.guardar(operacion);
                                    resp.setMensaje("devolver");
                                    resp.setId(valiResponse.getId());
                                }

                            } else {
                                // resp.setMensaje("No existen registros para la
                                // cuenta:"+in.getCuentaBeneficiario());
                                log.info("........No existen registros para la cuenta clabe:" + in.getCuentaBeneficiario()
                                        + ".........");
                                operacion.setMensaje_core("No existen registros para la clabe:");
                                operacion.setResponsecode(5);
                                abonoSpeiService.guardar(operacion);
                                resp.setMensaje("devolver");
                                resp.setId(5);
                            }
                            break;
                        default:
                            break;
                    }// Fin de switch

                } else {

                    // resp.setMensaje("Estatus no valido para operar para
                    // origen:"+origen_usuario.getIdorigen());
                    log.info(".............Estatus no valido para operar para origen:" + origen_usuario.getIdorigen()
                            + "...........");
                    resp.setMensaje("devolver");
                    resp.setId(3);
                }
            } else {
                // resp.setMensaje("Operacion fuera de horario");
                log.info(".......Operacion fuera de Horario.......");
                operacion.setMensaje_core("Operacion fuera de Horario");
                operacion.setResponsecode(2);
                abonoSpeiService.guardar(operacion);
                resp.setMensaje("devolver");
                resp.setId(2);
            }
        }
        return resp;

    }


    @Transactional
    public int realizarTransferencia(Auxiliar opa, request abono, int tipoop, int cargoabono) {
        int aplicados = 0;

        String sesion = funcionesSaiService.session();
        String referencia = abono.getTsLiquidacion() + abono.getClaveRastreo();
        try {
            System.out.println("vamos a realizar transferencia a opa:" + opa.getAuxiliarPK().getIdorigenp() + "-" + opa.getAuxiliarPK().getIdproducto() + "-" + opa.getAuxiliarPK().getIdauxiliar() + ",monto:" + abono.getMonto() + ",DescripcionAbono:" + abono);
            //Vamos a buscar si no se ha completado la que esta llegando
            TransferenciaCursoPK pk = new TransferenciaCursoPK(abono.getId(), abono.getCuentaBeneficiario(), abono.getClaveRastreo(), abono.getCuentaOrdenante(), cargoabono);
            TemporalTranferenciaCurso curso = transferenciaCursoService.buscarPorId(pk);

            if (curso != null) {
                System.out.println("::::::::::::::Operacion registrada con anterioridad::::::::::::::");
                aplicados = 1009;
            } else {
                temporal = new SpeiTemporal();
                curso = new TemporalTranferenciaCurso();
                curso.setPk(pk);
                curso.setOk_saicoop(false);
                curso.setMonto(abono.getMonto());
                matriz = origenesService.buscarMatriz();
                // Buscamos tabla para idcuenta
                tb_pk = new TablaPK(idtabla, "cuenta_contable");
                Tabla tb_cuenta_contable = tablasService.buscarPorId(tb_pk);
                tb_pk = new TablaPK(idtabla, "usuario");
                tb_usuario = tablasService.buscarPorId(tb_pk);


                // Vamos a registrar movimiento a producto abono(Abono)
                SpeiTemporalPK temporalPK = new SpeiTemporalPK();
                temporalPK.setIdoperacion(BigInteger.valueOf(abono.getId()));
                temporalPK.setIdorigenp(opa.getAuxiliarPK().getIdorigenp());
                temporalPK.setIdproducto(opa.getAuxiliarPK().getIdproducto());
                temporalPK.setIdauxiliar(opa.getAuxiliarPK().getIdauxiliar());
                temporalPK.setReferencia(referencia);

                //Vamos a buscar si no esta el registro en la tabla temporal
                // SpeiTemporal temporalTabla = speiTemporalService.buscarPorId(temporalPK);
               /* if (temporalTabla != null) {
                    log.info(":::::::::::::::::::Ya existe el registro en temporal por seguridad se omite la operacion::::::::::::::");
                    aplicados = -1;
                } else {*/
                temporal.setIdorigen(opa.getIdorigen());
                temporal.setIdgrupo(opa.getIdgrupo());
                temporal.setIdsocio(opa.getIdsocio());

                if (tipoop == 1) {
                    temporal.setConcepto_mov("SPEI ENTRADA REF:" + abono.getClaveRastreo());
                    temporal.setEsentrada(true);
                    //temporal.setEsentrada(true);
                } else if (tipoop == 2) {
                    temporal.setConcepto_mov("SPEI ENTRADA: Retroceso por timeout,REF:" + abono.getClaveRastreo());
                    //temporal.setEsentrada(false);
                    temporal.setEsentrada(false);
                } else if (tipoop == 3) {
                    log.info("Error poliza de retroceso");
                    temporal.setConcepto_mov("SPEI ENTRADA: Retroceso por falla general");
                    //temporal.setEsentrada(false);
                    temporal.setEsentrada(false);
                }
                temporal.setSpeiTemporalPK(temporalPK);
                temporal.setAcapital(abono.getMonto());
                //temporal.setSpeiTemporalPK();Referencia(abono.getTsLiquidacion() + abono.getClaveRastreo());//String.valueOf(abono.getId()));
                temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
                temporal.setSesion(sesion);
                //String sai = funcionesSaiService.sai_auxiliar(new AuxiliarPK(temporal.getIdorigenp(),temporal.getIdproducto(), temporal.getIdauxiliar()));
                //temporal.setSai_aux(sai);
                temporal.setMov(1);
                temporal.setTipopoliza(1);
                speiTemporalService.guardar(temporal);


                // Vamos a registrar el movimiento a cuentaContable(Cargo) para el balance
                temporal = new SpeiTemporal();
                temporalPK = new SpeiTemporalPK();
                temporalPK.setIdoperacion(BigInteger.valueOf(abono.getId().longValue()));
                temporalPK.setIdorigenp(0);
                temporalPK.setIdproducto(0);
                temporalPK.setIdauxiliar(0);
                temporalPK.setReferencia(referencia);

                temporal.setIdcuenta(tb_cuenta_contable.getDato1());
                temporal.setIdorigen(opa.getIdorigen());
                temporal.setIdgrupo(opa.getIdgrupo());
                temporal.setIdsocio(opa.getIdsocio());

                if (tipoop == 1) {
                    temporal.setConcepto_mov("SPEI ENTRADA REF:" + abono.getClaveRastreo());
                    //temporal.setEsentrada(false);
                    temporal.setEsentrada(false);
                } else if (tipoop == 2) {
                    temporal.setConcepto_mov("SPEI ENTRADA: Retroceso por timeout,REF:" + abono.getClaveRastreo());
                    //temporal.setEsentrada(true);
                    temporal.setEsentrada(true);
                } else if (tipoop == 3) {
                    temporal.setConcepto_mov("SPEI ENTRADA: Retroceso por falla general");
                    //temporal.setEsentrada(true);
                    temporal.setEsentrada(true);
                }
                temporal.setSpeiTemporalPK(temporalPK);
                temporal.setAcapital(abono.getMonto());
                //temporal.setReferencia(abono.getTsLiquidacion() + abono.getClaveRastreo());//String.valueOf(abono.getId()));
                temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
                temporal.setSesion(sesion);
                temporal.setMov(2);
                temporal.setTipopoliza(1);
                speiTemporalService.guardar(temporal);

                // Vamos a abonar a TDD si el cliente es CSN
                boolean banderaAlestra = false;//Solo mis compas de CSN
                if (matriz.getIdorigen() == 30200) {
                    if (tipoop == 1) {
                        log.info("Vamos a depositar a la TDD Alestra para opa:" + opa.getAuxiliarPK());
                        valiResponse = validaReglasCsn(opa, abono.getCuentaBeneficiario(), abono.getMonto(), abono.getFechaOperacion(), 1);
                        log.info(":::::::::::::::::::Valid response al realizar deposito::::::::::::::" + valiResponse.getId());
                        if (valiResponse.getMensaje().toUpperCase().contains("OK")) {
                            banderaAlestra = true;
                        }

                    } else {
                        log.info("Vamos a retirar de la TDD Alestra para opa:" + opa.getAuxiliarPK());
                        valiResponse = validaReglasCsn(opa, abono.getCuentaBeneficiario(), abono.getMonto(), abono.getFechaOperacion(), 2);
                        log.info(":::::::::::::::::::Valid response al realizar retiro::::::::::::::" + valiResponse.getId());
                        if (valiResponse.getMensaje().toUpperCase().contains("OK")) {
                            banderaAlestra = true;
                        }

                    }

                } else {
                    valiResponse.setId(999);
                    banderaAlestra = true; //Cualquier otra caja no usa TDD pero para que pase limpio en la validacion la pongo en true
                }

                log.info(":::::::::::Valor de Bandera:::::::::::::::" + banderaAlestra);
                if (valiResponse.getId() == 999 && banderaAlestra) {
                    // vamos a generar poliza(cargo cuenta spei y abono tdd)
                    try {
                        log.info(":::::::::::::::Generando poliza a SAICoop para opa :::::::::::::::::" + opa.getAuxiliarPK());
                        log.info("::::::::::::::::::::::::Ejecutando funcion para procesar registros de:" + tb_usuario.getDato1()+",sesion:"+sesion,temporalPK.getReferencia(), abono.getId());

                        Thread.sleep(1);
                        aplicados = funcionesSaiService.aplica_movs(
                                Integer.parseInt(tb_usuario.getDato1()),
                                temporal.getSesion(),
                                1,
                                temporal.getSpeiTemporalPK().getReferencia(),
                                String.valueOf(abono.getId()));
                        log.info("__________total aplicados transferencia spei___________:" + aplicados);

                    } catch (Exception e) {
                        log.error("::::::::::::::::::Sucedio error al realizar poliza en SAICoop::::::::::::::::::::::" + e.getMessage());
                    }

                    if (aplicados <= 0) {
                        //Si falla lo aplicado y el origen es CSN retiramos nuevamente de TDD
                        if (matriz.getIdorigen() == 30200) {
                            if (aplicados <= 0) {
                                log.info("::::::::::::::::Realizando retiro:::::::::::::::::::::::::");
                                log.info("::::::::::::::Vamos a retirar dinero de SyC hacia SAICoop,Por codigo en funcion -1001 o 0 ::::::::::::::::::::::");
                                valiResponse = validaReglasCsn(opa, "", abono.getMonto(), null, 2);
                            }/*else{
                                    log.warn(":::::::::::::::::::No se proceso nada porque funcion detecta que ya esta en registrado referencia:"+temporal.getReferencia()+":::::::::::::::::");
                                }*/

                        }
                    } else {
                        log.info("::::::::::::Se realizo poliza correcta nada que hacer:::::::::::::::::::::::::");
                        curso.setOk_saicoop(true);
                        //transferenciaCursoService.guardarMovimiento(curso);
                    }
                    // }
                }
                speiTemporalService.eliminar(sesion, abono.getTsLiquidacion() + abono.getClaveRastreo());
                //  log.info("________Elinar temporal::.");
                //TemporalPk temporalPk = new TemporalPk(Integer.parseInt(tb_usuario.getDato1()),sesion,abono.getTsLiquidacion()+abono.getClaveRastreo());
                //log.info("::___Llavev generada:"+temporalPk);
                //registroTemporalService.eliminarPorId(temporalPk);
            }
        } catch (Exception e) {
            speiTemporalService.eliminar(sesion, abono.getTsLiquidacion() + abono.getClaveRastreo());
            log.error("Error al realizar la transferencia spei:" + e.getMessage());
        }

        return aplicados;

    }

    private int realizarTransferenciaComision(Auxiliar opa, request in, int tipoop) {
        int aplicados = 0;
        try {
            String sesion = funcionesSaiService.session();
            log.info("Vamos a realizar movimientos para comision");
            /********************* Comision ************************/
            // Buscamos tabla para comision
            tb_pk = new TablaPK(idtabla, "monto_comision");
            Tabla tb_comision = tablasService.buscarPorId(tb_pk);

            // Buscamos tabla para producto comision
            tb_pk = new TablaPK(idtabla, "producto_comision");
            Tabla tb_producto_comision = tablasService.buscarPorId(tb_pk);

            // Buscamos tabla para producto iva comision
            tb_pk = new TablaPK(idtabla, "producto_iva_comision");
            Tabla tb_producto_iva_comision = tablasService.buscarPorId(tb_pk);
            tb_pk = new TablaPK(idtabla, "usuario");
            tb_usuario = tablasService.buscarPorId(tb_pk);
            matriz = origenesService.buscarMatriz();
            if (Double.parseDouble(tb_comision.getDato1()) > 0) {
                // Vamos a registrar movimiento a producto abono(cargo comision)
                temporal = new SpeiTemporal();
                temporal.setIdorigen(opa.getIdorigen());
                temporal.setIdgrupo(opa.getIdgrupo());
                temporal.setIdsocio(opa.getIdsocio());
                /*temporal.setIdorigenp(opa.getAuxiliarPK().getIdorigenp());
                temporal.setIdproducto(opa.getAuxiliarPK().getIdproducto());
                temporal.setIdauxiliar(opa.getAuxiliarPK().getIdauxiliar());*/
                if (tipoop == 1) {
                    temporal.setConcepto_mov("SPEI ENTRADA COMISION");
                    //temporal.setEsentrada(false);
                } else if (tipoop == 2) {
                    temporal.setConcepto_mov("SPEI ENTRADA COMISION: Retroceso por timeout");
                    //temporal.setEsentrada(true);select * from auxiliares_d where idorigenp=30202 and idproducto=133 and idauxiliar = 6756 order by fecha desc  limit 5
                } else if (tipoop == 3) {
                    temporal.setConcepto_mov("SPEI ENTRADA COMISION: Retroceso por falla general");
                    //temporal.setEsentrada(true);
                }

                temporal.setAcapital(Double.parseDouble(tb_comision.getDato1()) + (Double.parseDouble(tb_comision.getDato1())) * 0.16);
                //temporal.setReferencia(String.valueOf(in.getReferenciaNumerica()));
                temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
                temporal.setSesion(funcionesSaiService.session());
                //String sai_c = funcionesSaiService.sai_auxiliar(new AuxiliarPK(temporal.getIdorigenp(),temporal.getIdproducto(), temporal.getIdauxiliar()));
                //temporal.setSai_aux(sai_c);
                temporal.setMov(3);
                temporal.setTipopoliza(3);
                speiTemporalService.guardar(temporal);

                // Vamos a registrar movimiento al producto comision(abono )
                temporal = new SpeiTemporal();
                temporal.setIdorigen(opa.getIdorigen());
                temporal.setIdgrupo(opa.getIdgrupo());
                temporal.setIdsocio(opa.getIdsocio());
                // temporal.setIdproducto(Integer.parseInt(tb_producto_comision.getDato1()));
                temporal.setAcapital(Double.parseDouble(tb_comision.getDato1()));
                //temporal.setReferencia(String.valueOf(in.getReferenciaNumerica()));
                if (tipoop == 1) {
                    temporal.setConcepto_mov("SPEI ENTRADA COMISION");
                    //temporal.setEsentrada(true);
                } else if (tipoop == 2) {
                    temporal.setConcepto_mov("SPEI ENTRADA COMISION: Retroceso por timeout");
                    //temporal.setEsentrada(false);
                } else if (tipoop == 3) {
                    temporal.setConcepto_mov("SPEI ENTRADA COMISION: Retroceso por falla general");
                    //temporal.setEsentrada(false);
                }
                temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
                temporal.setSesion(funcionesSaiService.session());
                temporal.setMov(4);
                temporal.setTipopoliza(3);
                speiTemporalService.guardar(temporal);

                // Vamos a registrar movimiento al producto iva comision(Abono)
                // Producto producto_comision =
                // productoService.buscarPorId(Integer.parseInt(tb_producto_comision.getDato1()));
                temporal = new SpeiTemporal();
                temporal.setIdorigen(opa.getIdorigen());
                temporal.setIdgrupo(opa.getIdgrupo());
                temporal.setIdsocio(opa.getIdsocio());
                temporal.setIdcuenta(tb_producto_iva_comision.getDato1());
                if (tipoop == 1) {
                    temporal.setConcepto_mov("SPEI ENTRADA COMISION");
                    //temporal.setEsentrada(true);
                } else if (tipoop == 2) {
                    temporal.setConcepto_mov("SPEI ENTRADA COMISION: Retroceso por timeout");
                    //temporal.setEsentrada(false);
                } else if (tipoop == 3) {
                    temporal.setConcepto_mov("SPEI ENTRADA COMISION: Retroceso por falla general");
                    //temporal.setEsentrada(false);
                }
                temporal.setAcapital(Double.parseDouble(tb_comision.getDato1()) * 0.16);
                //temporal.setReferencia(String.valueOf(in.getReferenciaNumerica()));
                temporal.setIdusuario(Integer.parseInt(tb_usuario.getDato1()));
                temporal.setSesion(funcionesSaiService.session());
                temporal.setMov(5);
                temporal.setTipopoliza(3);
                speiTemporalService.guardar(temporal);

                if (matriz.getIdorigen() == 30200) {
                    if (tipoop == 1) {
                        valiResponse = validaReglasCsn(opa, "", new Double(tb_comision.getDato1()), in.getFechaOperacion(), 2);
                    } else {
                        valiResponse = validaReglasCsn(opa, "", new Double(tb_comision.getDato1()), in.getFechaOperacion(), 1);
                    }
                } else {
                    valiResponse.setId(999);
                }

                if (valiResponse.getId() == 999) {
                    AuxiliarPK Apk = null;//new AuxiliarPK(temporal.getIdorigenp(),temporal.getIdproducto(),temporal.getIdauxiliar());
                    aplicados = 0;// funcionesSaiService.aplica_movs(Integer.parseInt(tb_usuario.getDato1()), temporal.getSesion(), 3, temporal.getReferencia(), sendAbono().getId());
                    if (aplicados <= 0) {
                        if (matriz.getIdorigen() == 30200) {
                            log.info(".........Se volvio a depositar la comision a la TDD.......");
                            valiResponse = validaReglasCsn(opa, "", new Double(tb_comision.getDato1()), in.getFechaOperacion(), 1);

                        }
                        aplicados = 0;
                    }

                }
            }
            speiTemporalService.eliminar(sesion, String.valueOf(in.getReferenciaNumerica()));
        } catch (Exception e) {
            log.error("Error al realizar transferencia de comision:" + e.getMessage());
            aplicados = 0;
        }


        return aplicados;
    }

    public ConcResultadoVo conciliacion(Integer page, String tipoOrden, Integer fecha) {
        ConcResultadoVo resultado = new ConcResultadoVo();
        try {
            // Buscamos la empresa
            TablaPK tbPk = new TablaPK("stp", "empresa");
            Tabla tabla = tablasService.buscarPorId(tbPk);
            ConcPeticionVo conciliacionPet = new ConcPeticionVo();
            conciliacionPet.setPage(page);
            conciliacionPet.setTipoOrden(tipoOrden);
            if (fecha > 0) {
                conciliacionPet.setFechaOperacion(fecha);
            }
            if (tabla != null) {
                conciliacionPet.setEmpresa(tabla.getDato1());
                String firma = firmaPeticion(1, conciliacionPet, null, fecha);
                conciliacionPet.setFirma(firma);
                String peticion = json.toJson(conciliacionPet);
                //System.out.println("Peticion conciliacion::::::::::::::::::::::::::::::::" + peticion);
                String resultadoConciliacion = httpMethods.conciliacion(peticion);
                //System.out.println("Resultado conciliacion:::::::::::::::::::::::::::::::" + resultadoConciliacion);
                resultado = json.fromJson(resultadoConciliacion, ConcResultadoVo.class);
                if (resultado.getEstado() == 0) {
                    resultado.setCodigo(200);
                } else {
                    resultado.setCodigo(400);
                }
            } else {
                log.info(".............Empresa no definida............");
                resultado.setCodigo(400);
                resultado.setMensaje("Empresa no definida");
            }
        } catch (Exception e) {
            log.info("Error al obtener conciliacion:" + e.getMessage());
        }
        return resultado;
    }

    public ConcResultadoVo conciliacionHis(ConcPeticionVo conciliacionPet) {

        return null;
    }

    public SaldoResultadoVo consultaSaldo(String clabes, Integer fecha) {
        SaldoResultadoVo resultadoConsulta = new SaldoResultadoVo();
        try {
            ConsultaSaldoPet peticion = new ConsultaSaldoPet();

            TablaPK tbPk = new TablaPK("stp", "cuenta_concentradora");
            Tabla tablaCuentaConcentradora = tablasService.buscarPorId(tbPk);

            if (tablaCuentaConcentradora != null) {
                tbPk = new TablaPK("stp", "empresa");
                Tabla tabla = tablasService.buscarPorId(tbPk);
                peticion.setCuentaOrdenante(tablaCuentaConcentradora.getDato1().trim());
                peticion.setEmpresa(tabla.getDato1());
                if (fecha > 0) {
                    peticion.setFecha(fecha);
                }

                String firma = firmaPeticion(2, null, peticion, fecha);
                peticion.setFirma(firma);

                String peticionHttp = json.toJson(peticion);
                System.out.println("Peticion consulta Saldo::::::::::::::::::::" + peticion);
                String resultadoConsultaHttp = httpMethods.consultaSaldo(peticionHttp);
                System.out.println("Resultado consulta saldo::::::::::::::::::::::::" + resultadoConsultaHttp);
                resultadoConsulta = json.fromJson(resultadoConsultaHttp, SaldoResultadoVo.class);
                if (resultadoConsulta.getEstado() == 0) {
                    resultadoConsulta.setCodigo(200);
                } else {
                    resultadoConsulta.setCodigo(400);
                }
            } else {
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

    private response validaReglasCsn(Auxiliar opa, String clabe, Double monto, Integer fechaOperacion, Integer tipoOperacion) {
        response response = new response();
        response.setId(0);
        response.setMensaje("Error General");
        response.setCodigo(400);
        try {
            PersonaPK personaPk = new PersonaPK(opa.getIdorigen(), opa.getIdgrupo(), opa.getIdsocio());
            Persona persona = personaService.buscarPorId(personaPk);

            if (persona.getPk().getIdgrupo() == 10) {
                // Se han preparado los movimientos es hora de enviar a la tdd
                // Buscamos la tabla donde esta la url
                TablaPK tbpk = null;
                Tabla tabla = null;
                Origen matriz = origenesService.buscarMatriz();
                // Vamos a buscar si esta la tdd
                FolioTarjeta folioTarjeta = null;
                Tarjeta tarjeta = null;
                boolean bandera = false;
                switch (tipoOperacion) {
                    case 0:// Validacion reglas generales
                        // Buscamos minimo y maximo a operar
                        tbpk = new TablaPK(idtabla, "monto_minimo");
                        Tabla tb_minimo = tablasService.buscarPorId(tbpk);
                        tbpk.setIdElemento("monto_maximo");
                        Tabla tb_maximo = tablasService.buscarPorId(tbpk);
                        Double monto_minimo = new Double(tb_minimo.getDato1());
                        Double monto_maximo = new Double(tb_maximo.getDato1());

                        if (monto >= monto_minimo) {
                            if (monto <= monto_maximo) {
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
                                                    System.out.println("Fecha vencimiento");
                                                    if (tarjeta.getFecha_vencimiento().after(new Date())) {
                                                        tbpk.setIdElemento("monto_maximo_diario");
                                                        Tabla tb_monto_maximo_diario = tablasService.buscarPorId(tbpk);
                                                        Double acumulado = abonoSpeiService.montoDiario(fechaOperacion, clabe);

                                                        if ((acumulado + monto) < new Double(tb_monto_maximo_diario.getDato1())) {
                                                            response.setMensaje("OK");
                                                            response.setId(999);
                                                            response.setCodigo(200);
                                                        } else {
                                                            log.info(":::::::::::::el monto operado hoy supera el permitido en el core::::::::::");
                                                            response.setMensaje("El monto operado hoy supera el permitido en el core");
                                                            response.setId(16);
                                                        }

                                                    } else {
                                                        log.info("..............Tarjeta de debito esta vencida...........");
                                                        response.setMensaje("Tarjeta de Debito esta vencida");
                                                        response.setId(15);
                                                    }

                                                } else {
                                                    log.info(".............Tarjeta de Debito no encontrada..............");
                                                    response.setMensaje("Tarjeta de Debito no encontrada");
                                                    response.setId(15);
                                                }
                                            } else {
                                                log.info("............Sin registros para TDD...........");
                                                response.setMensaje("Sin registros para TDD");
                                                response.setId(14);
                                            }
                                        } else {
                                            log.info("............Clabe Inactiva...........");
                                            response.setMensaje("La Clabe esta inactiva");
                                            response.setId(14);
                                        }
                                    } else {
                                        log.info("............Clabe bloqueada...........");
                                        response.setMensaje("Clabe Bloqueada");
                                        response.setId(14);
                                    }

                                } else {
                                    log.info("............Clabe interbancaria vencida...........");
                                    response.setMensaje("Clabe interbancaria expirada");
                                    response.setId(14);
                                }
                            } else {
                                log.info(".........Monto es mayor al permitido en el core........");
                                response.setMensaje("Monto es mayor al permitido en el core");
                                response.setId(13);
                            }
                        } else {
                            log.info(".........Monto es menor al permitido en el core........");
                            response.setMensaje("Monto es menor al permitido en el core");
                            response.setId(6);
                        }
                        break;
                    case 1: // deposito a TDD
                        tbpk = new TablaPK(idtabla, "activar_uso_tdd");
                        tabla = tablasService.buscarPorId(tbpk);
                        if (tabla.getDato1().equals("1")) {
                            folioTarjeta = folioTarjetaService.buscarPorId(opa.getAuxiliarPK());
                            tarjeta = tarjetaService.buscarPorId(folioTarjeta.getIdtarjeta());
                            int maxIntentos = 4;
                            int intentos = 0;
                            boolean exito = false;

                            while (intentos < maxIntentos) {
                                try {
                                    intentos++;
                                    System.out.println("Intento deposito #" + intentos + ",para tarjeta:" + tarjeta.getIdtarjeta());
                                    // Suponiendo que esta función llama a la API externa

                                    //Fragemento solo para pruebas
                                    /*if (intentos == 3 && monto > 11) {
                                        bandera = true;
                                    } else {
                                        if (monto == 11) {
                                            bandera = true;
                                        } else {
                                            bandera = consumoCsnTDD.depositarSaldo(tabla.getDato2(), tarjeta.getIdtarjeta(), monto);
                                        }
                                    }*/
                                    //---Hasta aqui debe terminar solo para pruebas
                                    bandera = consumoCsnTDD.depositarSaldo(tabla.getDato2(), tarjeta.getIdtarjeta(), monto);
                                    if (bandera) {
                                        exito = true;
                                        break; // Salir del ciclo
                                    }

                                    // Si no fue true pero no hay error, esperar un poco y volver a intentar
                                    Thread.sleep(100);

                                } catch (Exception e) {
                                    System.err.println("Error en intento #" + intentos + ": " + e.getMessage());

                                    try {
                                        Thread.sleep(200); // Esperar antes de intentar de nuevo
                                    } catch (InterruptedException ie) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                }
                            }

                            if (!exito) {
                                System.err.println("No se logró la conexión tras " + maxIntentos + " intentos.");
                                response.setId(23);
                                response.setMensaje("Error al realizar deposito a TDD");
                                log.info(":::::::::::::::::::Error al realizar deposito a TDD::::::::::::::::::" + tarjeta.getIdtarjeta());
                            } else {
                                response.setId(999);
                                response.setMensaje("OK");
                                log.info(":::::::::Csn Deposito TDD exitoso:" + tarjeta.getIdtarjeta() + ",monto:" + monto + ":::::::");
                            }

                        } else {
                            response.setId(23);
                            response.setMensaje("Asegurate de activar servicios de Tarjeta de Debito");
                            log.info("Asegurate de activar los servicios de Tarjeta de Debito");
                        }
                        break;
                    case 2: // Retiro TDD
                        tbpk = new TablaPK(idtabla, "activar_uso_tdd");
                        tabla = tablasService.buscarPorId(tbpk);
                        if (tabla.getDato1().equals("1")) {
                            folioTarjeta = folioTarjetaService.buscarPorId(opa.getAuxiliarPK());
                            tarjeta = tarjetaService.buscarPorId(folioTarjeta.getIdtarjeta());

                            int maxIntentos = 4;
                            int intentos = 0;
                            boolean exito = false;

                            while (intentos < maxIntentos) {
                                try {
                                    intentos++;
                                    System.out.println("Intento retiro #" + intentos + ",para tarjeta:" + tarjeta.getIdtarjeta());
                                    // Suponiendo que esta función llama a la API externa

                                    //Fragemento solo para pruebas
                                   /* if (intentos == 3 && monto > 11) {
                                        bandera = true;
                                    } else {
                                        if (monto == 11) {
                                            bandera = true;
                                        } else {
                                            bandera = consumoCsnTDD.retirarSaldo(tabla.getDato2(), tarjeta.getIdtarjeta(), monto);
                                        }
                                    }*/
                                    //---Hasta aqui debe terminar solo para pruebas
                                    bandera = consumoCsnTDD.retirarSaldo(tabla.getDato2(), tarjeta.getIdtarjeta(), monto);
                                    if (bandera) {
                                        exito = true;
                                        break; // Salir del ciclo
                                    }

                                    // Si no fue true pero no hay error, esperar un poco y volver a intentar
                                    Thread.sleep(100);

                                } catch (Exception e) {
                                    System.err.println("Error en intento #" + intentos + ": " + e.getMessage());

                                    try {
                                        Thread.sleep(200); // Esperar antes de intentar de nuevo
                                    } catch (InterruptedException ie) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                }
                            }

                            if (exito) {
                                response.setId(999);
                                response.setMensaje("OK");
                                log.info(":::::::::Csn Retiro TDD exitoso:" + monto + ":::::::," + "tarjeta:" + tarjeta.getIdtarjeta());
                            } else {
                                response.setId(17);
                                response.setMensaje("Error al realizar retiro a TDD");
                                log.info("::::::::::::::::::Error al realizar retiro a TDD::::::::::::::::::::" + tarjeta.getIdtarjeta());
                            }
                        } else {
                            response.setId(18);
                            response.setMensaje("Asegurate de activar servicios de Tarjeta de Debito");
                            log.info("Asegurate de activar los servicios de Tarjeta de Debito");
                        }
                        break;
                    case 3:
                        log.info("Enviar sms");
                        //buscamos la parametrizacion servicio SMS
                        tbpk = new TablaPK(idtabla, "servicio_sms");
                        tabla = tablasService.buscarPorId(tbpk);
                        String url = tabla.getDato2();
                        if (tabla.getDato1().equals("1")) {
                            //Buscamos parametrizacion notificacion SMS
                            tbpk = new TablaPK(idtabla, "sms_notificacion");
                            tabla = tablasService.buscarPorId(tbpk);
                            Producto producto = productoService.buscarPorId(opa.getAuxiliarPK().getIdproducto());

                            Date fechaActual = new Date();
                            LocalDateTime fechaLocal = fechaActual.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");
                            String fechaFormateada = fechaLocal.format(formatter);

                            String mensaje = tabla.getDato2().replace("@total@", String.valueOf(monto)).replace("@nombreproducto@", producto.getNombre()).replace("@fecha@", fechaFormateada);
                            System.out.println("Mensaje formado:" + mensaje);

                            String respuesta_sms = new SMSCsn().enviarSMS(url, persona.getCelular(), mensaje);
                            System.out.println("Respuesta de sms:" + respuesta_sms);
                        }
                        break;
                }

            } else {
                log.info(".........Grupo para socio no permitido en el core........");
                response.setMensaje("Socio perteneciente a grupo no permitido en el core");
                response.setId(19);
            }

        } catch (Exception e) {
            log.info("....Error al validar reglas CSN..." + e.getMessage());
            response.setMensaje("Error al validar reglas CSN");
        }
        return response;
    }

    private response validaReglasMitras(Auxiliar a, Double monto, Integer fechaOperacion, String clabeBeneficiario) {
        response response = new response();
        response.setId(0);
        response.setMensaje("Error General");
        response.setCodigo(400);
        try {
            // Validamos que la persona se encuentre en los grupos validos
            PersonaPK personaPk = new PersonaPK(a.getIdorigen(), a.getIdgrupo(), a.getIdsocio());
            Persona persona = personaService.buscarPorId(personaPk);

            if (persona.getPk().getIdgrupo() == 10) {//|| persona.getPk().getIdgrupo() == 12) { //se omite grupo 12 el 26/12/2024
                // buscamos si esta en elgrupo 88 personas
                persona = personaService.buscarPorCurpGrupo(persona.getCurp(), 88);
                if (persona == null) {
                    // Confirmamo que siga bloqueado en sopar
                    Sopar sopar = soparService.buscarPorIdTipo(personaPk, "lista_personas_bloqueadas_cnbv");
                    if (sopar == null) {
                        // Buscamos minimo y maximo a operar
                        TablaPK tb_pk = new TablaPK(idtabla, "monto_minimo");
                        Tabla tb_minimo = tablasService.buscarPorId(tb_pk);
                        tb_pk.setIdElemento("monto_maximo");
                        Tabla tb_maximo = tablasService.buscarPorId(tb_pk);
                        Double monto_minimo = new Double(tb_minimo.getDato1());
                        Double monto_maximo = new Double(tb_maximo.getDato1());

                        if (monto >= monto_minimo) {
                            if (monto <= monto_maximo) {
                                // Buscamos la configuracion de producto para abono
                                tb_pk.setIdElemento("producto_abono");
                                Tabla tabla_producto_abono = tablasService.buscarPorId(tb_pk);
                                if (tabla_producto_abono != null) {
                                    // Validamos que el producto para abono configurado en tablas sea el mismo
                                    // relacionado a la clabe
                                    Producto producto_abono = productoService
                                            .buscarPorId(a.getAuxiliarPK().getIdproducto());

                                    if (producto_abono != null) {
                                        tb_pk.setIdElemento("monto_maximo_diario");
                                        Tabla tb_monto_maximo_diario = tablasService.buscarPorId(tb_pk);
                                        log.info("El total maximo diario es:" + tb_monto_maximo_diario.getDato1());
                                        log.info("Fecha operacion:" + fechaOperacion);
                                        Double acumulado = abonoSpeiService.montoDiario(fechaOperacion,
                                                clabeBeneficiario);

                                        log.info("Total acumulado:" + acumulado);

                                        log.info("El acumulado de hoy + monto operacion es:" + (acumulado + monto));

                                        if ((acumulado + monto) < new Double(tb_monto_maximo_diario.getDato1())) {
                                            log.info("Accedio aqui");
                                            log.info(String.valueOf(fechaOperacion).substring(0, 6));
                                            Double totalMes = abonoSpeiService.totalMes(clabeBeneficiario,
                                                    String.valueOf(fechaOperacion).substring(0, 6));
                                            System.out.println("Total acumulado en el mes:" + totalMes
                                                    + ",SELECT SUM(monto) FROM speirecibido WHERE LEFT(fechaoperacion::TEXT, 6) ="
                                                    + String.valueOf(fechaOperacion).substring(1, 6)
                                                    + " AND cuentabeneficiario= " + clabeBeneficiario
                                                    + " AND aplicado=true");
                                            tb_pk.setIdElemento("maximo_mes");
                                            Tabla tb_monto_maximo_mes = tablasService.buscarPorId(tb_pk);
                                            if ((totalMes + monto) <= Double
                                                    .parseDouble(tb_monto_maximo_mes.getDato1())) {
                                                response.setMensaje("OK");
                                                response.setId(999);

                                            } else {
                                                log.info("..........Limite mensual alcanzado..........");
                                                response.setMensaje("Ha alcanzado el limite mensual en el core : $"
                                                        + tb_monto_maximo_mes.getDato1());
                                                response.setId(17);
                                            }
                                        } else {
                                            log.info(
                                                    "..........el monto operado hoy supera el permitido en el core..........");
                                            response.setMensaje("El monto operado hoy supera el permitido en el core");
                                            response.setId(16);
                                        }
                                    } else {
                                        log.info(
                                                "..........Producto configurado como abono en tablas no corresponde a vinculado en clabes..........");
                                        response.setMensaje(
                                                "Producto configurado como abono en tablas no corresponde a vinculado en clabes");
                                        response.setId(15);
                                    }
                                } else {
                                    log.info("..........No existe configuracion de producto abono..........");
                                    response.setMensaje("No existe configuracion de producto abono");
                                    response.setId(14);
                                }
                            } else {
                                log.info(".........Monto es mayor al permitido en el core........");
                                response.setMensaje("Monto es mayor al permitido en el core");
                                response.setId(13);
                            }
                        } else {
                            log.info(".........Monto es menor al permitido en el core........");
                            response.setMensaje("Monto es menor al permitido en el core");
                            response.setId(6);
                        }
                    } else {
                        log.info(".........Socio bloqueado por CNBV........");
                        response.setMensaje("Socio bloqueado por CNBV");
                        response.setId(18);
                    }
                } else {
                    log.info("Socio bloqueado,grupo 88");
                }
            } else {
                log.info(".........Grupo para socio,no permitido para operar SPEI........");
                response.setMensaje("Grupo para socio,no permitido para operar SPEI........");
                response.setId(19);
            }

        } catch (Exception e) {
            log.info("....Error al validar reglas Mitras..." + e.getMessage());
            response.setMensaje("Error al validar reglas Mitras");
        }
        return response;
    }

    private response validaReglasFama(AuxiliarPK opa, Double monto, Integer fechaOperacion, String cuentaBeneficiario) {
        response response = new response();
        response.setId(0);
        response.setMensaje("Error General");
        response.setCodigo(400);
        try {
            // Buscamos minimo y maximo a operar
            TablaPK tb_pk = new TablaPK(idtabla, "monto_minimo");
            Tabla tb_minimo = tablasService.buscarPorId(tb_pk);
            Double monto_minimo = new Double(tb_minimo.getDato1());
            if (monto >= monto_minimo) {
                // Buscamos la configuracion de producto para abono
                tb_pk.setIdElemento("productos_abono");
                Tabla tabla_producto_abono = tablasService.buscarPorId(tb_pk);
                if (tabla_producto_abono != null) {
                    //Buscamos la lista de prestamos que estan realacionados a spei
                    boolean banderaCredito = false;
                    String[] productoCredito = tabla_producto_abono.getDato2().split("\\|");
                    for (int i = 0; i < productoCredito.length; i++) {
                        if (Integer.parseInt(productoCredito[i]) == opa.getIdproducto()) {
                            banderaCredito = true;
                        }
                    }

                    if (banderaCredito) {
                        response.setMensaje("OK");
                        response.setId(999);
                    } else {
                        log.info(
                                "..........Prestamo no apto para recibir pagos spei..........");
                        response.setMensaje("Prestamo no apto para recibir pagos spei");
                        response.setId(15);
                    }
                } else {
                    log.info("..........No existe configuracion de producto abono..........");
                    response.setMensaje("No existe configuracion de producto abono");
                    response.setId(14);
                }
            } else {
                log.info(".........Monto es menor al permitido en el core........");
                response.setMensaje("Monto es menor al permitido en el core");
                response.setId(6);
            }
        } catch (Exception e) {
            log.info("....Error al validar reglas Fama..." + e.getMessage());
            response.setMensaje("Error al validar reglas Fama");
        }
        return response;
    }


    private String firmaPeticion(Integer operacion, ConcPeticionVo conciliacion, ConsultaSaldoPet consultaSaldo, Integer fecha) {
        String firmada = "";
        try {
            StringBuilder sB = new StringBuilder();
            if (operacion == 1) {
                sB.append("||");
                sB.append(conciliacion.getEmpresa()).append("|");
                sB.append(conciliacion.getTipoOrden()).append("|");
                if (fecha > 0) {
                    sB.append(fecha);
                }
                sB.append("||");

            } else if (operacion == 2) {
                sB.append("||");
                sB.append(consultaSaldo.getEmpresa()).append("|");
                sB.append(consultaSaldo.getCuentaOrdenante()).append("|");
                if (fecha > 0) {
                    sB.append(fecha);
                }

                sB.append("||");

                log.info(sB.toString());
            }
            String cadena = sB.toString();
            firmada = sign(cadena);

        } catch (Exception e) {
            log.info("Error al firmar peticion:" + e.getMessage());
        }
        return firmada;
    }

    // Consigo mi firma
    public String sign(String cadena) throws Exception {
        String firmaCod;
        // Direccion de mi keystore local
        String fileName = ruta() + System.getProperty("file.separator") + "mitras"
                + System.getProperty("file.separator") + "caja_mitras.jks";// "/claves/cajamitras.jks";
        String password = "fenoreste2024";// "12345678";//"fenoreste2023";
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

    // Para obtener la ruta del servidor
    public static String ruta() {
        String home = System.getProperty("user.home");
        String separador = System.getProperty("file.separator");
        String actualRuta = home + separador + "CaSpei" + separador;
        return actualRuta;
    }



}
