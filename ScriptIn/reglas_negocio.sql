DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='hora_actividad';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2,dato3)VALUES('spei_entrada','hora_actividad','06:00','22:00','1|2|3|4|5');


/*Usuario para operar spei entrada*/
DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='usuario_operar';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','usuario','1111');
/*Usuario para usar ws
  1.- dato 1 = username
  2.- dato2 = password
*/
DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='usuario_ws';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2)VALUES('spei_entrada','usuario_ws','CSNSPEI','8a7f7b066908ab460de64e3a1b131808');

/*Monto minimo a operar como entrada*/
DELETE FROM tablas WHERE idtabla = 'spei_entrada' AND idelemento='monto_minimo';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_minimo','10');

/*Monto maximo a operar como entrada*/
 DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='monto_maximo';
 INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_maximo','200000');

 /*Monto maximo diario*/
 DELETE FROM tablas where idtabla='spei_entrada' AND idelemento='monto_maximo_diario';
 INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_maximo_diario','3000');

 /*Producto para abonar la comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='producto_comision';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','producto_comision','5040');

 /*Producto para abonar iva de comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='producto_iva_comision';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','producto_iva_comision','5040');

/*Monto para comision*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='monto_comision';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','monto_comision','5.00');

/*Cuenta contable cargos*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='cuenta_contable';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','cuenta_contable','10703070101054');

/*Url servicio tdd*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='url_tdd';
INSERT INTO tablas(idtabla,idelemento,dato2)VALUES('spei_entrada','url_tdd','http://192.168.99.37:8080/WSDL_TDD_CSN/');

/*Uso servicio TDD*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento='activar_desactivar_tdd';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_entrada','activar_desactivar_tdd','0');


/*Producto a donde se mandara el dinero*/
DELETE FROM tablas WHERE idtabla='spei_entrada' AND idelemento = 'producto_abono';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('spei_entrada','producto_abono','130');


