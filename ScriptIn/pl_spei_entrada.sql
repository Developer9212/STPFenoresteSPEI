


create or replace function
spei_entrada_servicio_activo_inactivo () returns boolean as $$
declare
  d_fecha_servidor  date;
  d_fecha_origenes  date;
  b_estatus_oper    boolean;
  t_hora_ini        text;
  t_hora_fin        text;
begin
  select
  into   t_hora_ini, t_hora_fin dato1, dato2
  from   tablas
  where  idtabla = 'spei_entrada' and idelemento = 'hora_actividad';
  
  if sai_findstr(t_hora_ini,':') = 0 or sai_findstr(t_hora_fin,':') = 0 or
     split_part(t_hora_ini,':',1) > split_part(t_hora_fin,':',1)
  then
    raise notice 'MAL DEFINIDAS LAS HORAS EN LA TABLA: spei_entrada / hora_actividad';
    return NULL;
  end if;

  d_fecha_servidor := date(now());
  
  select
  into   d_fecha_origenes, b_estatus_oper date(fechatrabajo), estatus
  from   origenes
  limit  1;
  
  return not ( d_fecha_servidor != d_fecha_origenes or
               current_time::time not between t_hora_ini::time and t_hora_fin::time or
               not b_estatus_oper );
end;
$$ language 'plpgsql';






create or replace function
sai_spei_entrada_aplica (integer, text,integer,text) returns integer as $$
declare
  p_idusuario         alias for $1;
  p_sesion            alias for $2;
  p_tipopoliza        alias for $3;
  p_referencia        alias for $4;

  d_fecha_hoy         date;
  i_idorigen_ap       integer;
  t_periodo           text;
  i_tp_pol            integer;
  i_idpoliza          integer;
  i_movs              integer;
  t_poliza_generada   text;
  t_concepto          text;
  r_temp              record;
begin

  -- Tipo poliza:
  i_tp_pol := p_tipopoliza;


   RAISE NOTICE 'El valor de la variable es: %', i_tp_pol;
  insert
  into   temporal
         (idusuario,sesion,idorigen,idgrupo,idsocio,idorigenp,idproducto,idauxiliar,esentrada,acapital,io_pag,io_cal,im_pag,im_cal,aiva,
          saldodiacum,abonifio,idcuenta,ivaio_pag,ivaio_cal,ivaim_pag,ivaim_cal,mov,tipomov,referencia,diasvencidos,montovencido,idorigena,
          huella_valida,concepto_mov,fe_nom_archivo,fe_xml,sai_aux,fecha_hora_mov)
         (select idusuario,sesion,idorigen,idgrupo,idsocio,idorigenp,idproducto,idauxiliar,esentrada,acapital,io_pag,io_cal,im_pag,im_cal,aiva,
                 saldodiacum,abonifio,idcuenta,ivaio_pag,ivaio_cal,ivaim_pag,ivaim_cal,mov,tipomov,referencia,diasvencidos,montovencido,idorigena,
                 huella_valida,concepto_mov,fe_nom_archivo,fe_xml,sai_aux,fecha_hora_system
          from   spei_entrada_temporal_cola_guardado
          where  idusuario = p_idusuario and sesion = p_sesion);

  select
  into   d_fecha_hoy date(fechatrabajo)
  from   origenes
  limit  1;

  select
  into   i_idorigen_ap idorigen
  from   usuarios
  where  idusuario = p_idusuario;
  
  t_periodo := trim(to_char(d_fecha_hoy,'yyyymm'));

  i_idpoliza = sai_folio(TRUE,'pol'||t_periodo||trim(to_char(i_idorigen_ap,'099999'))||i_tp_pol::text);

  UPDATE usuarios
  SET    --pingreso = d_fecha_hoy::text||'|'||i_idorigen_ap||'|1|'||i_idpoliza::text
         ticket = ticket + 1
  WHERE  idusuario = p_idusuario;

  if(i_tp_pol = 1) THEN 
     t_concepto := (select concepto_mov
          from   spei_entrada_temporal_cola_guardado
          where  idusuario = p_idusuario and sesion = p_sesion and i_tp_pol = p_tipopoliza ORDER by fecha_inserta DESC LIMIT 1); --'SPEI Entrada';
     ELSE 
     t_concepto := (select concepto_mov
          from   spei_entrada_temporal_cola_guardado
          where  idusuario = p_idusuario and sesion = p_sesion and i_tp_pol = p_tipopoliza ORDER by fecha_inserta DESC LIMIT 1); --'Comision SPEI Entrada';
   END IF;
  
  

  t_poliza_generada := i_idorigen_ap||'-'||t_periodo||'-'||i_tp_pol::text||'-'||i_idpoliza::text;

  i_movs := sai_temporal_procesa(p_idusuario,p_sesion,d_fecha_hoy,i_idorigen_ap,i_idpoliza,i_tp_pol,t_concepto,FALSE,FALSE);

  update spei_entrada_temporal_cola_guardado
  set    poliza_generada = t_poliza_generada, aplicado = TRUE, fecha_aplicado = d_fecha_hoy + clock_timestamp()::time
  where  idusuario = p_idusuario and sesion = p_sesion;
  
  -- DELETE FROM al "termporal"

   DELETE FROM temporal WHERE idusuario = p_idusuario AND sesion = p_sesion;
 --ELETE FROM spei_entrada_temporal_cola_guardado WHERE sesion = p_sesion AND idusuario = p_idusuario AND referencia = p_referencia;
  
  return i_movs;
end;
$$ language 'plpgsql';



CREATE OR REPLACE FUNCTION funcion_activa() 
RETURNS BOOLEAN AS $$
DECLARE
    funcion_activa BOOLEAN;
BEGIN
    LOOP
        -- Consulta si la función está activa en pg_stat_activity

        SELECT EXISTS (
            SELECT 1
            FROM pg_stat_activity
            WHERE current_query LIKE '%sai_spei_entrada_aplica%'  -- Quita la condición state
        ) INTO funcion_activa;

       RAISE NOTICE 'La función aactiva:%',funcion_activa;    

        -- Si no está activa, sale del bucle y continúa
        IF NOT funcion_activa THEN
            EXIT;
        END IF;

        -- Si está activa, espera 15 segundos antes de volver a verificar
        PERFORM pg_sleep(15);
    END LOOP;

    -- Aquí coloca el código que deseas ejecutar cuando la función termine
    RAISE NOTICE 'La función ha terminado su ejecución. Continuando...';

    -- Retorno final de la función
    RETURN funcion_activa;  -- O FALSE, según el caso
END;
$$ LANGUAGE plpgsql;



