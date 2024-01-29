drop table if exists spei_entrada_temporal_cola_guardado cascade;

create table spei_entrada_temporal_cola_guardado
  (
    id integer,
    fecha_inserta       timestamp without time zone default clock_timestamp() not null,
    aplicado            boolean                     default false,
    idusuario           integer,
    sesion              character varying(20),
    idorigen            integer,
    idgrupo             integer,
    idsocio             integer,
    idorigenp           integer,
    idproducto          integer,
    idauxiliar          integer,
    esentrada           boolean,
    acapital            numeric(12,2)               default 0.00 not null,
    io_pag              numeric(10,2)               default 0.00 not null,
    io_cal              numeric(10,2)               default 0.00 not null,
    im_pag              numeric(10,2)               default 0.00 not null,
    im_cal              numeric(10,2)               default 0.00 not null,
    aiva                numeric(10,2)               default 0.00 not null,
    saldodiacum         numeric(12,2)               default 0.00 not null,
    abonifio            numeric(10,2)               default 0.00 not null,
    idcuenta            character varying(20)       default '0'  not null,
    ivaio_pag           numeric(10,2)               default 0.00 not null,
    ivaio_cal           numeric(10,2)               default 0.00 not null,
    ivaim_pag           numeric(10,2)               default 0.00 not null,
    ivaim_cal           numeric(10,2)               default 0.00 not null,
    mov                 integer,
    tipomov             integer                     default 0    not null,
    referencia          text,
    diasvencidos        integer                     default 0    not null,
    montovencido        numeric(12,2)               default 0    not null,
    idorigena           integer                     default 0    not null,
    huella_valida       boolean                     default false,
    concepto_mov        text,
    fe_nom_archivo      text,
    fe_xml              text,
    sai_aux             text,
    fecha_hora_system   timestamp without time zone default now(),
    poliza_generada     text,
    fecha_aplicado      timestamp without time zone,
    tipopoliza              integer
  );

create index spei_entrada_temporal_cola_guardado_idx
on           spei_entrada_temporal_cola_guardado
             (fecha_inserta);


create index spei_entrada_temporal_cola_guardado_idx on spei_entrada_temporal_cola_guardado (fecha_inserta);


DROP TABLE IF EXISTS speirecibido;
CREATE TABLE speirecibido(
 id                       Integer,	 
 fechaoperacion           Integer,
 institucionordenante     Integer,
 institucionbeneficiaria  Integer,
 claverastreo             text,
 monto                    numeric,
 nombreordenante          text,
 tipocuentaordenante      Integer,
 cuentaordenante          text,
 rfccurpordenante         text,
 nombrebeneficiario       text,
 tipocuentabeneficiario   Integer,
 cuentabeneficiario       text,
 rfcCurpbeneficiario      text,
 conceptopago             text,
 referencianumerica       Integer,
 empresa                  text,
 fechaentrada             timestamp without time zone,
 responsecode             integer,
 mensajeerror             text,
 aplicado                 boolean default false,
 fechaprocesada           timestamp without time zone,
 
 primary key(id)
 );

 /*
 alter table speirecibido add column 
 
 */




create sequence sec_spei_temporal
  start with 1
  increment by 1
  maxvalue 99999
  minvalue 1;

