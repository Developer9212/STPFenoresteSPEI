drop table if exists spei_entrada_temporal_cola_guardado cascade;
create table spei_entrada_temporal_cola_guardado
(
    id                integer,
    fecha_inserta     timestamp             not null default clock_timestamp(),
    aplicado          boolean                        default false,
    idusuario         integer,
    sesion            character varying(20),
    idorigen          integer,
    idgrupo           integer,
    idsocio           integer,
    idorigenp         integer,
    idproducto        integer,
    idauxiliar        integer,
    esentrada         boolean,
    acapital          numeric(12, 2)        not null default 0.00,
    io_pag            numeric(10, 2)        not null default 0.00,
    io_cal            numeric(10, 2)        not null default 0.00,
    im_pag            numeric(10, 2)        not null default 0.00,
    im_cal            numeric(10, 2)        not null default 0.00,
    aiva              numeric(10, 2)        not null default 0.00,
    saldodiacum       numeric(12, 2)        not null default 0.00,
    abonifio          numeric(10, 2)        not null default 0.00,
    idcuenta          character varying(20) not null default '0',
    ivaio_pag         numeric(10, 2)        not null default 0.00,
    ivaio_cal         numeric(10, 2)        not null default 0.00,
    ivaim_pag         numeric(10, 2)        not null default 0.00,
    ivaim_cal         numeric(10, 2)        not null default 0.00,
    mov               integer,
    tipomov           integer               not null default 0,
    referencia        text,
    diasvencidos      integer               not null default 0,
    montovencido      numeric(12, 2)        not null default 0,
    idorigena         integer               not null default 0,
    huella_valida     boolean                        default false,
    concepto_mov      text,
    fe_nom_archivo    text,
    fe_xml            text,
    sai_aux           text,
    fecha_hora_system timestamp                      default now(),
    poliza_generada   text,
    fecha_aplicado    timestamp,
    tipopoliza        integer,
    idoperacion       int8,
    query             text,
    primary key (idorigenp, idproducto, idauxiliar, esentrada, idoperacion)
);



/*
create index spei_entrada_temporal_cola_guardado_idx
    on spei_entrada_temporal_cola_guardado
        (fecha_inserta);

create index spei_entrada_temporal_cola_guardado_idx on spei_entrada_temporal_cola_guardado (fecha_inserta);*/



DROP TABLE IF EXISTS speirecibido;
CREATE TABLE speirecibido
(
    id                      Integer,
    fechaoperacion          Integer,
    institucionordenante    Integer,
    institucionbeneficiaria Integer,
    claverastreo            text,
    monto                   numeric,
    nombreordenante         text,
    tipocuentaordenante     Integer,
    cuentaordenante         text,
    rfccurpordenante        text,
    nombrebeneficiario      text,
    tipocuentabeneficiario  Integer,
    cuentabeneficiario      text,
    rfcCurpbeneficiario     text,
    conceptopago            text,
    referencianumerica      Integer,
    empresa                 text,
    fechaentrada            timestamp without time zone,
    responsecode            integer,
    mensaje_core            text,
    aplicado                boolean default false,
    fechaprocesada          timestamp without time zone,
    retardo                 boolean default false,
    stp_ok                  boolean default false,
    tsliquidacion           text,

    primary key (id,claverastreo,tsliquidacion)
);



DROP TABLE IF EXISTS transferencias_spei_curso;
CREATE TABLE transferencias_spei_curso
(
    id              integer,
    clabe           text,
    claverastreo    text,
    cuentaordenante text,
    ok_saicoop      boolean,
    monto           numeric,
    cargoabono       integer
);



create sequence sec_spei_temporal
    start with 1
    increment by 1
    maxvalue 99999
    minvalue 1;


DROP TABLE IF EXISTS speireibido_duplicados;
CREATE TABLE speirecibido_duplicados(
     id                      Integer,
     fechaoperacion          Integer,
     institucionordenante    Integer,
     institucionbeneficiaria Integer,
     claverastreo            text,
     monto                   numeric,
     nombreordenante         text,
     cuentaordenante         text,
     nombrebeneficiario      text,
     cuentabeneficiario      text,
     rfcCurpbeneficiario     text,
     conceptopago            text,
     referencianumerica      Integer,
     fechaentrada            timestamp without time zone,
     responsecode            integer,
     mensaje_core            text

);

