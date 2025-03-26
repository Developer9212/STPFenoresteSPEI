DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='hora_actividad';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2,dato3)VALUES('spei_entrada','hora_actividad','07:00','22:00','1|2|3|4|5');


/*Usuario para operar spei entrada*/
DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='usuario';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','usuario','999');
/*Usuario para usar ws
  1.- dato 1 = username
  2.- dato2 = password
*/
DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='usuario_ws';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2)VALUES('spei_entrada','usuario_ws','famatest','famatest');

/*Monto minimo a operar como entrada*/
DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='monto_minimo';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_minimo','1000');

/*Prestamos abono*/
DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='productos_abono';
INSERT INTO tablas(idtabla,idelemento,dato2)VALUES('spei_entrada','productos_abono','30202|3023|30304');


/*Producto para abonar la comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='producto_comision';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','producto_comision','526');

 /*Producto para abonar iva de comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='producto_iva_comision';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','producto_iva_comision','20407090101003 ');

/*Monto para comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='monto_comision';






INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_comision','0');

/*Cuenta contable cargos*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='cuenta_contable';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','cuenta_contable','10102020102016');
/*Producto a donde se mandara el dinero*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento = 'productos_abono';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('spei_entrada','producto_abono','130|');


/*Datos para conciliacion*/
DELETE FROM tablas WHERE idtabla='stp' AND idelemento='empresa';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('stp','empresa','CAJA_MITRAS');

DELETE FROM tablas WHERE idtabla='conciliacion' AND idelemento='stppath';
INSERT INTO tablas(idtabla,idelemento,dato2) VALUES('conciliacon','stppath','https://efws-dev.stpmex.com/efws/API/V2')


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