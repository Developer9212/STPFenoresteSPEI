
DELETE FROM tablas where idtabla='spei_salida' AND idelemento='producto_spei';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_salida','producto_spei','133');


DELETE FROM tablas where idtabla='spei_salida' AND idelemento='cuenta_comision';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2,dato3)VALUES('spei_salida','cuenta_comision','40309010101014','5.00','1');

DELETE FROM tablas where idtabla='spei_salida' AND idelemento='max_udis_transferencia';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_salida','max_udis_transferencia','1500');

DELETE FROM tablas where idtabla='spei_salida' AND idelemento='max_udis_transferencia';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_salida','max_udis_transferencia','1500');


DELETE FROM tablas where idtabla='spei_salida' AND idelemento='cuenta_contable';
INSERT INTO tablas(idtabla,idelemento,dato1)VALUES('spei_salida','cuenta_contable','10703070101054');

DELETE FROM tablas where idtabla='spei_salida' AND idelemento='activar_uso_tdd';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2)VALUES('spei_salida','activar_uso_tdd','1','https://csn.coop:8085/WSDL_TDD_CSN/');
