DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='hora_actividad';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2,dato3)VALUES('spei_entrada','hora_actividad','06:00','21:00','1|2|3|4|5');

DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='usuario';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','usuario','1111');

DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='usuario_ws';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2)VALUES('spei_entrada','usuario_ws','CSNSPEI','8a7f7b066908ab460de64e3a1b131808');

/*Monto minimo a operar como entrada*/
DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='monto_minimo';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_minimo','50');

/*Monto maximo a operar como entrada*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='monto_maximo';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_maximo','200000');


DELETE FROM tablas where idtabla='spei_entrada' AND idelemento='monto_maximo_diario';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_maximo_diario','200000');

DELETE FROM tablas where idtabla='spei_entrada' AND idelemento='cuenta_contable';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','cuenta_contable','10703070101054');

/*SOLO PARA CSN
1.- dato1 = 1 activar 0 desactivar
2.- dato2 = URLTDD
*/
DELETE FROM tablas where idtabla='spei_entrada' AND idelemento='activar_uso_tdd';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2)VALUES('spei_entrada','activar_uso_tdd','1','https://csn.coop:8085/WSDL_TDD_CSN/');

/*--------------------------------------------------------------------------------------------------*/

/*Producto para abonar la comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='producto_comision';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','producto_comision','5040');

/*Producto para abonar iva de comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='producto_iva_comision';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','producto_iva_comision','20407090101004');

/*Monto para comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='monto_comision';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_comision','5.00');


/*SMS abono*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='sms_notificacion';
INSERT INTO tablas(idtabla,idelemento,dato2)VALUES('spei_entrada','sms_notificacion','Se abono un total de @total@ a la @nombreproducto@ el dia @fecha@');

/*Servicio SMS*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='servicio_sms';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2)VALUES('spei_entrada','servicio_sms','1','http://192.168.15.50/CSNsms/action.php?mensaje=_mensaje&numero=_numero');

/*Ultima actualizacion 08/11/2024*/

