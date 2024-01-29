
DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='spei_monto_maximo';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_salida','spei_monto_maximo','20000');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='stppath';
INSERT INTO tablas(idtabla,idelemento,dato2)VALUES('spei_salida','stppath','http://192.168.15.127:7001/csn/');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='spei_horario_actividad';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2,dato3) VALUES('spei_salida','horario_actividad','06:00','17:00','1|2|3|4|5');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='cuenta';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_salida','cuenta','20407160101068');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='cuenta_iva_comision';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('spei_salida','cuenta_iva_comision','20407090101004');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='sms_actualizar_estado';
INSERT INTO tablas(idtabla,idelemento,dato2) VALUES('spei_salida','sms_actualizar_estado','@fechayHora@ @idorden@ @estado@ @folio@ @causadevolucion@');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='usuario';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('spei_salida','usuario','999');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento ='cuenta_comision';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2) VALUES ('spei_salida','cuenta_comisio','40309010101014','5.00');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='monto_minimo';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('spei_salida','monto_minimo','10.0');
