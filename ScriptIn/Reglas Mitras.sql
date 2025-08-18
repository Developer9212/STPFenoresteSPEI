DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='hora_actividad';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2,dato3)VALUES('spei_entrada','hora_actividad','06:00','22:00','1|2|3|4|5');


/*Usuario para operar spei entrada*/
DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='usuario_operar';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','usuario','1400');
/*Usuario para usar ws
  1.- dato 1 = username
  2.- dato2 = password
*/
DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='usuario_ws';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2)VALUES('spei_entrada','usuario_ws','mitrasspei_prod','mitrasspei_prod');

/*Monto minimo a operar como entrada*/
DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='monto_minimo';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_minimo','50');

/*Monto maximo a operar como entrada*/
 DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='monto_maximo';
 INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_maximo','200000');

 /*Monto maximo diario*/
 DELETE FROM tablas where idtabla='spei_entrada' AND idelemento='monto_maximo_diario';
 INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_maximo_diario','200000');

 /*Producto para abonar la comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='producto_comision';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','producto_comision','5040');

 /*Producto para abonar iva de comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='producto_iva_comision';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','producto_iva_comision','20407090101004');

/*Monto para comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='monto_comision';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_comision','5.00');

/*Cuenta contable cargos*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='cuenta_contable';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','cuenta_contable','10703070101054');

/*Producto a donde se mandara el dinero*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento = 'producto_abono';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('spei_entrada','producto_abono','130');

/*Maximo por Mes*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento = 'maximo_mes';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('spei_entrada','maximo_mes','100');


/*Datos para conciliacion*/
DELETE FROM tablas WHERE idtabla='stp' AND idelemento='empresa';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('stp','empresa','CAJA_MITRAS');

/*Datos para consulta de saldo*/
DELETE FROM tablas WHERE idtabla='stp' AND idelemento='cuenta_concentradora';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('stp','cuenta_concentradora','646180518700000008');

DELETE FROM tablas WHERE idtabla='conciliacion' AND idelemento='stppath';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2) VALUES('conciliacion','stppath','/efws/API/V2','https://efws-dev.stpmex.com');

DELETE FROM tablas WHERE idtabla='conciliacion' AND idelemento='consulta_saldo_stppath';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2) VALUES('conciliacion','consulta_saldo_stppath','/efws/API','https://efws-dev.stpmex.com');

DELETE FROM TABLAS WHERE idtabla='spei_entrada' AND idelemento='datos_firma';
INSERT INTO TABLAS(idtabla,idelemento,dato1,dato2,dato3) VALUES('spei_entrada','datos_firma','caja_mitras.jks','fenoreste2024.','caja_mitras')

/*
 curl --header "Content-Type: application/json"    -H "Authorization: Basic $(echo -n 'speitest:speitest' | base64)"   --request POST   --data '{{
    "id": 56,
 "fechaOperacion": 20200127,
 "institucionOrdenante": 846,
 "institucionBeneficiaria": 90646,
 "claveRastreo": "12345",
 "monto": 5,
 "nombreOrdenante": "STP",
 "tipoCuentaOrdenante": 40,
 "cuentaOrdenante": "846180000400000001",
 "rfcCurpOrdenante": "ND",
 "nombreBeneficiario": "NOMBRE_DE_BENEFICIARIO",
 "tipoCuentaBeneficiario": 40,
 "cuentaBeneficiario": "646180518701130320",
 "nombreBeneficiario2": "NOMBRE_DE_BENEFICIARIO2",
 "tipoCuentaBeneficiario2": 40,
 "cuentaBeneficiario2": "64618012340000000D",
 "rfcCurpBeneficiario": "ND",
 "conceptoPago": "PRUEBA1",
 "referenciaNumerica": 1234567,
 "empresa": "NOMBRE_EMPRESA",
 "tipoPago":1,
 "tsLiquidacion": "1634919027297",
 "folioCodi": "f4c1111abd2b28a00abc"
}' http://tuip:8086/spei/api/sendAbono  


*/