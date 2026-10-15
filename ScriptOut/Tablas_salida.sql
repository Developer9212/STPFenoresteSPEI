
DROP TABLE IF EXISTS speienviado;
CREATE TABLE speienviado(
                            id                     integer,
                            institucioncontraparte text,
                            empresa                text,
                            claverastreo           text,
                            idorigenp              integer,
                            idproducto             integer,
                            idauxiliar             integer,
                            institucionoperante    integer,
                            monto                  numeric,
                            tipopago               integer,
                            tipocuentaordenante    integer,
                            nombreordenante        text,
                            cuentaordenante        text,
                            rfccurpordenante       text,
                            tipocuentabeneficiario integer,
                            nombrebeneficiario     text,
                            cuentabeneficiario     text,
                            rfccurpbeneficiario    text,
                            conceptopago           text,
                            referencianumerica     integer,
                            fechaentrada           timestamp without time zone,
                            fechaejecucion         timestamp without time zone,
                            idorden                integer,
                            aplicado               boolean default false,
                            estatus                text,
                            mensaje_core           text,
                            fecha_actualizacion_estado timestamp without time zone,

                primary key (id)
);

create sequence sec_spei_enviado
    start with 1
    increment by 1
    maxvalue 99999
    minvalue 1;



 DROP TABLE IF EXISTS estadosordenspei;
 CREATE TABLE estadosordenspei(
     id integer,
   	 empresa varchar(30),
   	 folioorigen text,
   	 claverastreo text,
   	 estado varchar(30),
   	 causadevolucion text,
   	 tsliquidacion text,
     fechaactualizacion timestamp without time zone

 );
