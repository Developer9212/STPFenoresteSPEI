


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

