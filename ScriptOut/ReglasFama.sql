
DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='clabe_operaciones';
INSERT INTO tablas(idtabla,idelemento,nombre,dato1)VALUES('spei_salida','clabe_operaciones','clabe que se usa para todas las operaciones','646180522900000001');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='empresa';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_salida','empresa','CAJA_FAMA');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='spei_horario_actividad';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2,dato3) VALUES('spei_salida','horario_actividad','06:00','17:00','1|2|3|4|5');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='cuenta';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_salida','cuenta','20407160101068');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='usuario';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('spei_salida','usuario','999');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='stppath';
INSERT INTO tablas(idtabla,idelemento,dato2)VALUES('spei_salida','stppath','https://demo.stpmex.com/speiws/rest');

//Datos certificado Dato1 alias,dato2 ruta certificado,dato3 nombre llavero,dato4 contraseña llavero
DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='datos_certificado';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2,dato3,dato4)VALUES('spei_salida','datos_certificado','caja-fama-qa','Certificados/speisalida/','famaQa','cajafamaqa');








DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento='cuenta_iva_comision';
INSERT INTO tablas(idtabla,idelemento,dato1) VALUES('spei_salida','cuenta_iva_comision','20407090101004');

DELETE FROM tablas WHERE idtabla='spei_salida' AND idelemento ='cuenta_comision';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2) VALUES ('spei_salida','cuenta_comisio','40309010101014','5.00');


