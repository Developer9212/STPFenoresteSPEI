

update origenes set fechatrabajo='24/06/2025'


select * from ws_siscoop_clabe_interbancaria where clabe = '646010132301232006'

select * from speirecibido where fechaoperacion=20250122

select * from auxiliares_d where idorigenp=30202 and idproducto=133 and idauxiliar = 6756 order by fecha desc  limit 5

select * from auxiliares_d where idorigenp=30223 and idproducto=133 and idauxiliar=2791 order by fecha desc limit 10

select * from ws_siscoop_clabe_interbancaria where idorigenp=30223 and idproducto=133 and idauxiliar=2791







delete from temporal
select idusuario,sesion,referencia,aplicado from temporal

15 segundos -- Si el web service saicoop responde confirmar y en menos de 15 stp la liquida si no devolver
5 sgundos --- ws valida speirecibido si es la misma operacion de ser asi va y busca como esta, si esta aplicada con retardo y stp_ok esta en false
             simeplenmente el json devuelve confirmar
			 
			 


select * from spei_entrada_temporal_cola_guardado
delete from spei_entrada_temporal_cola_guardado

select * from ws_siscoop_clabe_interbancaria where clabe='646010132301201121'
select * from auxiliares_d where idorigenp=30291 and idproducto=133 and idauxiliar=1100 order by fecha desc limit 5

select * from ws_siscoop_clabe_interbancaria where clabe='646010132301208616'
select * from auxiliares_d where idorigenp=30218 and idproducto=133 and idauxiliar=9531 order by fecha desc limit 5

select * from ws_siscoop_clabe_interbancaria where clabe='646010132301200436'
select * from auxiliares_d where idorigenp=30210 and idproducto=133 and idauxiliar=7346 order by fecha desc limit 5

select * from ws_siscoop_clabe_interbancaria where clabe='646010132301232006'
select * from auxiliares_d where idorigenp=30202 and idproducto=133 and idauxiliar=6756 order by fecha desc limit 5







24034-180625::::::::::234890725

select * from tablas where idtabla='spei_entrada'

update tablas set dato1='04:00' where idtabla='spei_entrada' AND idelemento='hora_actividad'

select * from polizas where concepto like '%SPEI%5737%'

select sum(monto) from auxiliares_d where idorigenp=30202 and idproducto=133 and idauxiliar = 6756 and date(fecha)= '22/01/2025'

select * from polizas where idorigenc = 30257 and periodo='202506' and idtipo=1 and idpoliza=211

update polizas set concepto = 'SPEI ENTRADA: Retroceso por timeout,REF:1997' where idorigenc = 30257 and periodo='202412' and idtipo=1 and idpoliza=24

select curp from personas where idorigen = 30226 and idgrupo=10 and idsocio= 368236

SELECT * FROM tablas where idtabla ='spei_entrada'
0302261036823


SELECT *  FROM ws_siscoop_clabe cb where asignada = true order by fecha_vencimiento desc
SELECT w.*  FROM ws_siscoop_clabe_interbancaria w INNER JOIN ws_siscoop_clabe cb USING(clabe) where cb.fecha_vencimiento > (select distinct fechatrabajo from origenes limit 1)

ALTER table speirecibido add column tsliquidacion text

select * from speirecibido_duplicados



SELECT * FROM polizas where idusuario = 1111

select * from ws_siscoop_clabe_interbancaria



SELECT conname 
FROM pg_constraint 
WHERE conrelid = 'speirecibido'::regclass AND contype = 'p';




select * from tablas where idtabla='bankingly_banca_movil'



select * from tablas WHERE idtabla='tasas' and idelemento like '2%' order by idelemento;


select *, from tablas WHERE idtabla='tasas' and idelemento like 'tasaiar%' order by idelemento;

select * from productos where idproducto=205

en CSN esa te da una lista de las tasas




delete from speirecibido


select * from tablas where idtabla='spei_entrada'

select * from transferencias_spei_curso
select * from speirecibido order by aplicado
select count(*) from speirecibido
select count(*) from speirecibido where aplicado = true
select * from spei_entrada_temporal_cola_guardado order by sesion

SELECT pid, state, wait_event_type, wait_event, query
FROM pg_stat_activity
WHERE datname = 'sannicolasdb';



delete from spei_entrada_temporal_cola_guardado;
delete from transferencias_spei_curso;
delete from speirecibido;

delete from temporal

SELECT count(*) FROM pg_stat_activity;
SELECT * FROM pg_stat_activity WHERE state != 'idle';
SELECT pid, datname, usename, client_addr, application_name, state, query
FROM pg_stat_activity;

SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE state = 'idle' AND now() - state_change > interval '5 minutes';

SELECT pid, datname, usename, application_name, client_addr, state, query
FROM pg_stat_activity
ORDER BY state;




select sai_spei_entrada_aplica()


drop table if exists spei_entrada_temporal_cola_guardado cascade;
create table spei_entrada_temporal_cola_guardado (
  id                 integer,
  fecha_inserta      timestamp              not null  default clock_timestamp(),
  aplicado           boolean                          default false,
  idusuario          integer,
  sesion             character varying(20),
  idorigen           integer,
  idgrupo            integer,
  idsocio            integer,
  idorigenp          integer,
  idproducto         integer,
  idauxiliar         integer,
  esentrada          boolean,
  acapital           numeric(12,2)          not null  default 0.00,
  io_pag             numeric(10,2)          not null  default 0.00,
  io_cal             numeric(10,2)          not null  default 0.00,
  im_pag             numeric(10,2)          not null  default 0.00,
  im_cal             numeric(10,2)          not null  default 0.00,
  aiva               numeric(10,2)          not null  default 0.00,
  saldodiacum        numeric(12,2)          not null  default 0.00,
  abonifio           numeric(10,2)          not null  default 0.00,
  idcuenta           character varying(20)  not null  default '0',
  ivaio_pag          numeric(10,2)          not null  default 0.00,
  ivaio_cal          numeric(10,2)          not null  default 0.00,
  ivaim_pag          numeric(10,2)          not null  default 0.00,
  ivaim_cal          numeric(10,2)          not null  default 0.00,
  mov                integer,
  tipomov            integer                not null  default 0,
  referencia         text,
  diasvencidos       integer                not null  default 0,
  montovencido       numeric(12,2)          not null  default 0,
  idorigena          integer                not null  default 0,
  huella_valida      boolean                          default false,
  concepto_mov       text,
  fe_nom_archivo     text,
  fe_xml             text,
  sai_aux            text,
  fecha_hora_system  timestamp                        default now(),
  poliza_generada    text,
  fecha_aplicado     timestamp,
  tipopoliza         integer,
  idoperacion        int8,
  query              text,
  primary key (idorigenp,idproducto,idauxiliar,referencia,idoperacion)
);


create or replace function
sai_spei_entrada_aplica (integer,text,integer,text,text) returns integer as $$
declare
  p_idusuario         alias for $1;
  p_sesion            alias for $2;
  p_tipopoliza        alias for $3;
  p_referencia        alias for $4;
  p_idop              alias for $5; /*Se agrego el 13/12/2024 Wilmer para una eliminacion segura se obtiene error cargos ! de abonos*/

  d_fecha_hoy         date;
  i_idorigen_ap       integer;
  t_periodo           text;
  i_tp_pol            integer;
  i_idpoliza          integer;
  i_movs              integer;
  t_poliza_generada   text;
  t_concepto          text;
  r_temp              record;
  query_delete        text;

  -- Agregado el 11/12/2024 -- Wilmer

  t_movs_ref  integer;
begin

  select
  into   d_fecha_hoy date(fechatrabajo)
  from   origenes
  limit  1;

  select
  into   i_idorigen_ap idorigen
  from   usuarios
  where  idusuario = p_idusuario;

  -- Tipo poliza:
  i_tp_pol := p_tipopoliza;

  i_movs := sai_spei_entrada_desglosa_prestamo (d_fecha_hoy, p_idusuario, p_sesion, p_referencia);
  
  i_movs := sai_spei_entrada_redistribuye_prestamo (d_fecha_hoy, p_idusuario, p_sesion, p_referencia);

  -- SE HIZO UNA CORRECCION EN LOS CAMPOS io_cal Y saldodiacum PORQUE SE ESTABAN
  -- DEJANDO EN CERO PERO ESO CAUSABA UN ERROR A FIN DE MES, EN EL PAGO DEL
  -- INTERES AL AHORRO, YA QUE SE MOVIA LA fechaumi DEL AUXILIAR Y SE ELIMINABA
  -- LO QUE SE TENIA GUARDADO EN EL CAMPO DE INTERES (JFPA, 25/ABRIL/2024)
  insert into temporal
    (idusuario,sesion,idorigen,idgrupo,idsocio,idorigenp,idproducto,idauxiliar,esentrada,acapital,io_pag,
     io_cal,
     im_pag,im_cal,aiva,
     saldodiacum,
     abonifio,idcuenta,ivaio_pag,ivaio_cal,ivaim_pag,ivaim_cal,mov,tipomov,referencia,diasvencidos,montovencido,
     idorigena,huella_valida,concepto_mov,fe_nom_archivo,fe_xml,sai_aux,fecha_hora_mov)
    (select idusuario,sesion,idorigen,idgrupo,idsocio,idorigenp,idproducto,idauxiliar,esentrada,acapital,
            io_pag,
            (case when sai_aux is not NULL and sai_aux != ''
                  then case when split_part(sai_aux,'|',1)::integer in (0,1,8)
                            then split_part(sai_aux,'|',2)::numeric
                            else io_cal
                       end
                  else io_cal
             end),
            im_pag, im_cal,aiva,
            (case when sai_aux is not NULL and sai_aux != ''
                  then case when split_part(sai_aux,'|',1)::integer = 0
                            then split_part(sai_aux,'|',5)::numeric
                            when split_part(sai_aux,'|',1)::integer = 1
                            then split_part(sai_aux,'|',8)::numeric
                            when split_part(sai_aux,'|',1)::integer = 8
                            then split_part(sai_aux,'|',9)::numeric
                            else saldodiacum
                       end
                  else saldodiacum
             end),
            abonifio,idcuenta,ivaio_pag,ivaio_cal,ivaim_pag,ivaim_cal,mov,tipomov,referencia,diasvencidos,montovencido,
            idorigena,huella_valida,concepto_mov,fe_nom_archivo,fe_xml,sai_aux,fecha_hora_system
     from   spei_entrada_temporal_cola_guardado
     where  idusuario = p_idusuario and sesion = p_sesion and referencia = p_referencia and idoperacion = p_idop::bigint);

  t_periodo := trim(to_char(d_fecha_hoy,'yyyymm'));

  i_idpoliza = sai_folio(TRUE,'pol'||t_periodo||trim(to_char(i_idorigen_ap,'099999'))||i_tp_pol::text);

  --lock table usuarios in row exclusive mode;

  UPDATE usuarios
  SET    ticket = ticket + 1
  WHERE  idusuario = p_idusuario;

  if(i_tp_pol = 1) THEN
    t_concepto := (select concepto_mov
                   from   spei_entrada_temporal_cola_guardado
                   where  idusuario = p_idusuario and sesion = p_sesion and i_tp_pol = p_tipopoliza
                   ORDER by fecha_inserta DESC LIMIT 1); --'SPEI Entrada';
  ELSE
    t_concepto := (select concepto_mov
                   from   spei_entrada_temporal_cola_guardado
                   where  idusuario = p_idusuario and sesion = p_sesion and i_tp_pol = p_tipopoliza ORDER by fecha_inserta DESC LIMIT 1); --'Comision SPEI Entrada';
  END IF;

  t_poliza_generada := i_idorigen_ap||'-'||t_periodo||'-'||i_tp_pol::text||'-'||i_idpoliza::text;

  i_movs := sai_temporal_procesa(p_idusuario,p_sesion,d_fecha_hoy,i_idorigen_ap,i_idpoliza,i_tp_pol,t_concepto,FALSE,FALSE);
  
RAISE NOTICE 'Mensaje: %', i_movs;

  -- DELETE FROM al "termporal"
   DELETE FROM temporal WHERE idusuario = p_idusuario AND sesion = p_sesion AND referencia = p_referencia;
/*
  update spei_entrada_temporal_cola_guardado
  set    poliza_generada  = t_poliza_generada,
         aplicado         = TRUE,
         fecha_aplicado   = d_fecha_hoy + clock_timestamp()::time,
         query            = 'DELETE FROM spei_entrada_temporal_cola_guardado WHERE sesion = '||
                            p_sesion||' AND idusuario ='||p_idusuario||' AND referencia ='||p_referencia||
                            ' AND idoperacion ='||p_idop
  where  idusuario = p_idusuario and sesion = p_sesion;*/


  DELETE FROM spei_entrada_temporal_cola_guardado
  WHERE sesion = p_sesion AND idusuario = p_idusuario AND referencia = p_referencia AND idoperacion = p_idop::bigint;
  
  /*IF (i_movs > 0) THEN  
     DELETE FROM spei_entrada_temporal_cola_guardado
     WHERE sesion = p_sesion AND idusuario = p_idusuario AND referencia = p_referencia AND idoperacion = p_idop::bigint;
  END IF;*/
  
    
  return i_movs;
end;
$$ language 'plpgsql';