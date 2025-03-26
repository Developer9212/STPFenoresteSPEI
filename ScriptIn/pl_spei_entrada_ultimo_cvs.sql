/*

CREACION DE TABLA DE PASO DESDE INICIO:

-- drop table if exists spei_entrada_temporal_cola_guardado cascade;
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
  primary key (idorigenp,idproducto,idauxiliar,esentrada,idoperacion)
);

*/
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

/*
create or replace function
sai_spei_entrada_aplica (integer,text,integer,text) returns integer as $$
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

  -- SE HIZO UNA CORRECCION EN LOS CAMPOS io_cal Y saldodiacum PORQUE SE ESTABAN
  -- DEJANDO EN CERO PERO ESO CAUSABA UN ERROR A FIN DE MES, EN EL PAGO DEL
  -- INTERES AL AHORRO, YA QUE SE MOVIA LA fechaumi DEL AUXILIAR Y SE ELIMINABA
  -- LO QUE SE TENIA GUARDADO EN EL CAMPO DE INTERES (JFPA, 25/ABRIL/2024)
  insert
  into   temporal
         (idusuario,sesion,idorigen,idgrupo,idsocio,idorigenp,idproducto,idauxiliar,esentrada,acapital,io_pag,
          io_cal,
          im_pag,im_cal,aiva,
          saldodiacum,
          abonifio,idcuenta,ivaio_pag,ivaio_cal,ivaim_pag,ivaim_cal,mov,tipomov,referencia,diasvencidos,
          montovencido,idorigena,huella_valida,concepto_mov,fe_nom_archivo,fe_xml,sai_aux,fecha_hora_mov)
         (select idusuario,sesion,idorigen,idgrupo,idsocio,idorigenp,idproducto,idauxiliar,esentrada,acapital,io_pag,
                 case when sai_aux is not NULL and sai_aux != '' then split_part(sai_aux,'|',2)::numeric else io_cal end,
                 im_pag,im_cal,aiva,
                 case when sai_aux is not NULL and sai_aux != '' then split_part(sai_aux,'|',5)::numeric else saldodiacum end,
                 abonifio,idcuenta,ivaio_pag,ivaio_cal,ivaim_pag,ivaim_cal,mov,tipomov,referencia,diasvencidos,
                 montovencido,idorigena,huella_valida,concepto_mov,fe_nom_archivo,fe_xml,sai_aux,fecha_hora_system
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

  lock table usuarios in row exclusive mode;
  UPDATE usuarios
  SET    ticket = ticket + 1
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
--  NOTA: Wilmer manipula en su codigo...
--  DELETE FROM spei_entrada_temporal_cola_guardado WHERE sesion = p_sesion AND idusuario = p_idusuario AND referencia = p_referencia;
  
  return i_movs;
end;
$$ language 'plpgsql';
*/

create or replace function
sai_spei_entrada_prestamo_cuanto(integer,integer,integer,date,integer,text) returns text as $$
begin
/*p_idorigenp   alias for $1;
  p_idproducto  alias for $2;
  p_idauxiliar  alias for $3;
  p_fecha       alias for $4;
  p_ta          alias for $5;
  p_aux         alias for $6;  */
  return sai_bankingly_prestamo_cuanto($1,$2,$3,$4,$5,$6);
end;
$$ language 'plpgsql';


create or replace function
sai_spei_entrada_prestamo_adelanto_exacto (integer,integer,integer,date,numeric,numeric) returns numeric as $$
begin
/*p_idorigenp       alias for $1,
  p_idproducto      alias for $2;
  p_idauxiliar      alias for $3;
  p_fecha           alias for $4;
  p_monto_a_pagar   alias for $5;
  p_monto_a_cubrir  alias for $6;  */
  return sai_bankingly_prestamo_adelanto_exacto ($1,$2,$3,$4,$5,$6);
end;
$$ language 'plpgsql';


create or replace function
sai_spei_entrada_desglosa_prestamo (date,integer,varchar,varchar) returns integer as $$
declare
  p_fecha               alias for $1;
  p_idusuario           alias for $2; 
  p_sesion              alias for $3;
  p_referencia          alias for $4;
  r_mov                 record;
  r_tab                 record;
  r_paso                record;
  r_seg                 record;
  r_aux_ah              record;
  r_aux_pr              record;
  r_com                 record;
  t_aux                 text;
  t_cuanto              text;
  t_adelanto            text;
  t_nom_arch            text;
  b_hay_adelint         boolean;
  b_adelantar_a_int     boolean;
  b_reg_resultado       boolean;
  b_hay_detalles        boolean;
  n_suma_total          numeric;
  n_montoven            numeric;
  n_proxabono           numeric;
  n_sobrante            numeric;
  n_aio_spai            numeric;
  n_aivaio_spai         numeric;
  n_dif                 numeric;
  n_total_io_iva_spai   numeric;
  n_adelanto_capital    numeric;
  n_montoseg            numeric;
  n_a_monto_seguro      numeric;
  n_a_iva_seguro        numeric;
  n_monto_mov           numeric;
  n_tasa_iva_io         numeric;
  n_a_monto_comision    numeric;
  n_a_iva_comision      numeric;
  i_idorigenp           integer;
  i_idproducto          integer;
  i_idauxiliar          integer;
  i_prod_destino        integer;
  i_prod_resum          integer;
  i_paso                integer;
  i_dv                  integer;
  n_comnopag            numeric;
  t_ref_temporal        text;
  
begin

  select
  into   r_tab *
  from   tablas
  where  idtabla = 'param' and idelemento = 'cobrar_interes_hasta_el_sig_pago' and dato1 = '1' and
         dato2 is not NULL and dato2::integer > 0;
  b_hay_adelint := found;

  -- TERMINAN VALIDACIONES PREVIAS -----------------------------------------------------------------
  raise notice ' ';
  raise notice '-----------------------------------------------------------------------------------------------';
  raise notice ' ';
  raise notice 'SE COMENZARA A DESGLOSAR PRESTAMO:';

  n_suma_total := 0;
  n_montoseg   := 0;
  n_montoven   := 0;
  n_proxabono  := 0;
  n_comnopag   := 0;

  select
  into   i_prod_resum coalesce(dato1::integer,0)
  from   tablas
  where  idtabla = 'spei_entrada' and idelemento = 'producto_resumidero';
  if not found then
    i_prod_resum := 0;
  end if;

  for r_mov
  in  select   mca.*, a.tipoamortizacion as ta, a.tasaio, a.idorigen, a.idgrupo, a.idsocio, a.saldo
      from     spei_entrada_temporal_cola_guardado mca
               inner join auxiliares a using(idorigenp,idproducto,idauxiliar)
               inner join productos  p using(idproducto)
      where    mca.fecha_inserta::date = p_fecha and mca.idusuario = p_idusuario and mca.sesion = p_sesion and
               mca.referencia = p_referencia and mca.idorigenp > 0 and mca.idproducto > 0 and mca.idauxiliar > 0 and
               mca.esentrada and p.tipoproducto = 2  --(esentrada = cargoabono = 1)
      order by esentrada
  loop
    t_ref_temporal := NULL;

    raise notice ' ';
    raise notice ' + CREDITO: %-%-%, IMPORTE DESTINADO: %',
                 r_mov.idorigenp, r_mov.idproducto, r_mov.idauxiliar, r_mov.acapital;
  
    if r_mov.sai_aux is NULL or r_mov.sai_aux = '' then
      t_aux := sai_auxiliar(r_mov.idorigenp, r_mov.idproducto, r_mov.idauxiliar, p_fecha);
    else
      t_aux := r_mov.sai_aux;
    end if;

    -- CUANTO DEBE EL PRESTAMO -----------------------------
    --  retorna: n_suma|n_montoseg|n_montoven|n_io|n_ivaio|n_im|n_ivaim|n_proxabono|n_comnopag|split_part(p_aux,'|',1);

    t_cuanto := sai_spei_entrada_prestamo_cuanto(r_mov.idorigenp, r_mov.idproducto, r_mov.idauxiliar, p_fecha, r_mov.ta, t_aux);

    n_suma_total := split_part(t_cuanto,'|',1)::numeric;
    n_montoseg   := split_part(t_cuanto,'|',2)::numeric;
    n_montoven   := split_part(t_cuanto,'|',3)::numeric;
    n_proxabono  := split_part(t_cuanto,'|',8)::numeric;
    n_comnopag   := split_part(t_cuanto,'|',9)::numeric;
    
    -- ------------------------------------------------------

    n_monto_mov := r_mov.acapital;
    n_a_monto_comision := 0;

    if n_comnopag > 0 then
      select
      into   r_paso *
      from   tablas
      where  idtabla = 'param' and idelemento = 'nueva_comision_por_atraso';
      if found then
        for r_com
        in  select idproducto_comision, monto_comision
            from   comision_por_atraso (r_mov.idorigenp, r_mov.idproducto, r_mov.idauxiliar, p_fecha, t_aux)
        loop
          n_tasa_iva_io := sai_iva_segun_sucursal(r_mov.idorigenp, r_com.idproducto_comision, 0); -- el nombre de la variable, no importa, NO son int. ordinarios
          if r_com.monto_comision >= n_monto_mov then
            n_a_monto_comision  := round(n_monto_mov / (1 + (n_tasa_iva_io / 100)), 2);
            n_a_iva_comision    := n_monto_mov - n_a_monto_comision;
          else
            n_a_monto_comision  := round(r_com.monto_comision / (1 + (n_tasa_iva_io / 100)), 2);
            n_a_iva_comision    := r_com.monto_comision - n_a_monto_comision;
          end if;
          n_monto_mov  := n_monto_mov  - (n_a_monto_comision + n_a_iva_comision);
          n_suma_total := n_suma_total - (n_a_monto_comision + n_a_iva_comision);

          insert
          into   spei_entrada_temporal_cola_guardado
                 (fecha_inserta, idusuario, sesion, referencia, idorigen, idgrupo, idsocio, idorigenp, idproducto, idauxiliar,
                  esentrada, acapital, iva, sai_aux)
          values (r_mov.fecha_inserta, r_mov.idusuario, r_mov.sesion, r_mov.referencia, r_mov.idorigen, r_mov.idgrupo, r_mov.idsocio,        
                  0, r_com.idproducto_comision, 0, TRUE, n_a_monto_comision, n_a_iva_comision, '');
          
          exit when n_monto_mov = 0;
        end loop;

        if n_monto_mov = 0 then

          raise notice '   > TODO EL IMPORTE QUE ERA DESTINADO AL CREDITO, PASO A COMISIONES';

          -- Si comisiones chupo todo el importe, eliminar movimiento de prestamo ----------
          delete
          from   spei_entrada_temporal_cola_guardado
          where  fecha_inserta = r_mov.fecha_inserta and idusuario = r_mov.idusuario and sesion = r_mov.sesion and
                 referencia = r_mov.referencia and idorigenp = r_mov.idorigenp and idproducto = r_mov.idproducto and
                 idauxiliar = r_mov.idauxiliar;

        else

          raise notice '   > POR LAS COMISIONES, EL IMPORTE ORIGINAL DEL CREDITO: % SE REDUJO A: %',
                       r_mov.acapital, n_monto_mov;
          update spei_entrada_temporal_cola_guardado
          set    acapital = n_monto_mov, sai_aux = case when sai_aux is NULL or sai_aux = '' then t_aux else sai_aux end
          where  fecha_inserta = r_mov.fecha_inserta and idusuario = r_mov.idusuario and sesion = r_mov.sesion and
                 referencia = r_mov.referencia and idorigenp = r_mov.idorigenp and idproducto = r_mov.idproducto and
                 idauxiliar = r_mov.idauxiliar;
        end if;
      end if;
    end if;

    continue when n_monto_mov = 0;

    n_sobrante  := 0;
    if r_mov.ta = 5 then
      if n_montoseg > 0 then
        for r_seg
        in  select   *
            from     sai_prestamos_hipotecarios_calcula_seguro_a_pagar (r_mov.idorigenp, r_mov.idproducto, r_mov.idauxiliar, p_fecha)
            order by idorigenpr, idproductor, idauxiliarr
        loop
          if (r_seg.apagar + r_seg.ivaapagar) >= n_monto_mov then
            n_tasa_iva_io     := sai_iva_segun_sucursal(r_mov.idorigenp, r_seg.idproductor, 0); -- el nombre de la variable, no importa, NO son int. ordinarios
            n_a_monto_seguro  := round(n_monto_mov / (1 + (n_tasa_iva_io / 100)), 2);
            n_a_iva_seguro    := n_monto_mov - n_a_monto_seguro;
          else
            n_a_monto_seguro  := r_seg.apagar;
            n_a_iva_seguro    := r_seg.ivaapagar;
          end if;
          n_monto_mov  := n_monto_mov  - (n_a_monto_seguro + n_a_iva_seguro);
          n_suma_total := n_suma_total - (n_a_monto_seguro + n_a_iva_seguro);

          -- Inserta Seguro ----
          raise notice '   > DESTINANDO AL SEGURO HIPOTECARIO: %-%-%, IMPORTE DESTINADO: %',
                       r_seg.idorigenpr, r_seg.idproductor, r_seg.idauxiliarr, n_a_monto_seguro;
          insert
          into   spei_entrada_temporal_cola_guardado
                 (fecha_inserta, idusuario, sesion, referencia, idorigen, idgrupo, idsocio, idorigenp, idproducto,
                  idauxiliar, esentrada, acapital, aiva, sai_aux)
          values (r_mov.fecha_inserta, r_mov.idusuario, r_mov.sesion, r_mov.referencia, r_mov.idorigen, r_mov.idgrupo,
                  r_mov.idsocio, r_seg.idorigenpr, r_seg.idproductor, r_seg.idauxiliarr, TRUE, n_a_monto_seguro,
                  n_a_iva_seguro, '');

          exit when n_monto_mov = 0;
        end loop;

        if n_monto_mov = 0 then

          raise notice '   > TODO EL IMPORTE QUE ERA DESTINADO AL CREDITO, PASO A SEGUROS';

          -- Si el seguro chupo todo el importe, eliminar movimiento de prestamo ----------
          delete
          from   spei_entrada_temporal_cola_guardado
          where  fecha_inserta = r_mov.fecha_inserta and idusuario = r_mov.idusuario and sesion = r_mov.sesion and
                 referencia = r_mov.referencia and idorigenp = r_mov.idorigenp and idproducto = r_mov.idproducto and
                 idauxiliar = r_mov.idauxiliar;

        else
          -- Modifica el monto del prestamo, rebaja los seguros, deja lo que sobro
          if n_monto_mov > n_suma_total then
            n_sobrante := sai_spei_entrada_prestamo_adelanto_exacto (r_mov.idorigenp, r_mov.idproducto, r_mov.idauxiliar,
                                                                     p_fecha, n_monto_mov, n_suma_total);
          end if;

          raise notice '   > POR LOS SEGUROS, EL IMPORTE ORIGINAL DEL CREDITO: % SE REDUJO A: %',
                       r_mov.acapital, n_monto_mov - n_sobrante;

          update spei_entrada_temporal_cola_guardado
          set    acapital = n_monto_mov - n_sobrante,
                 sai_aux = case when sai_aux is NULL or sai_aux = '' then t_aux else sai_aux end
          where  fecha_inserta = r_mov.fecha_inserta and idusuario = r_mov.idusuario and sesion = r_mov.sesion and
                 referencia = r_mov.referencia and idorigenp = r_mov.idorigenp and idproducto = r_mov.idproducto and
                 idauxiliar = r_mov.idauxiliar;
        end if;
      else
        if n_monto_mov > n_suma_total then
          n_sobrante := sai_spei_entrada_prestamo_adelanto_exacto (r_mov.idorigenp, r_mov.idproducto, r_mov.idauxiliar,
                                                                   p_fecha, n_monto_mov, n_suma_total);
          update spei_entrada_temporal_cola_guardado
          set    acapital = n_monto_mov - n_sobrante,
                 sai_aux = case when sai_aux is NULL or sai_aux = '' then t_aux else sai_aux end
          where  fecha_inserta = r_mov.fecha_inserta and idusuario = r_mov.idusuario and sesion = r_mov.sesion and
                 referencia = r_mov.referencia and idorigenp = r_mov.idorigenp and idproducto = r_mov.idproducto and
                 idauxiliar = r_mov.idauxiliar;
        end if;
      end if;

      -- Es el sobrante que no se puede adelantar, por no completar el capital de una amortizacion
      continue when n_sobrante = 0;

    else  -- r_mov.ta != 5
      raise notice '   > CREDITO SIN MODIFICAR, PASA COMPLETO !!';

      -- Si no hay sobrante, brinca a otro registro --------
      continue when (n_monto_mov - n_suma_total) <= 0;
      -- ---------------------------------------------------
      -- Si el monto_movimiento es mas alla de lo que debe y su saldo total, ya no hagas adelato a interes
      -- sobrante va ahorro
      if n_monto_mov >= ((r_mov.saldo - (n_montoven + n_proxabono)) + n_suma_total) then
        b_adelantar_a_int := FALSE;
        raise notice '   > POR SU MONTO EXCEDENTE, PODRA LIQUIDARSE EN SU TOTALIDAD !!';
        n_sobrante := n_monto_mov - ((r_mov.saldo - (n_montoven + n_proxabono)) + n_suma_total);
      else
        b_adelantar_a_int := TRUE;
        n_sobrante := n_monto_mov - n_suma_total;
      end if;

      -- SI HAY SOBRANTE Y TIENE OPCION A ADELANTO DE INTERES:
      if b_hay_adelint and b_adelantar_a_int then

        t_adelanto := monto_interes_para_siguiente_fecha_de_pago(r_mov.idorigenp, r_mov.idproducto, r_mov.idauxiliar,
                                                                 p_fecha, n_montoven + n_proxabono, r_mov.tasaio);
        -- Total de adelanto a interes original (spai: siguiente pago a intereres)
        n_total_io_iva_spai  := split_part(t_adelanto,'|',3)::numeric;

        -- En caso de que no haber adelanto por ser la misma fecha u otra cosa, brinca a otro registro
        continue when n_total_io_iva_spai = 0;
        
        if (n_sobrante - n_total_io_iva_spai >= 0) then
          n_aio_spai          := split_part(t_adelanto,'|',1)::numeric;
          n_aivaio_spai       := split_part(t_adelanto,'|',2)::numeric;
        else
          n_tasa_iva_io       := sai_iva_segun_sucursal(r_mov.idorigenp, r_mov.idproducto, 0);
          n_aio_spai          := round(n_sobrante / ((n_tasa_iva_io / 100) + 1), 2);
          n_aivaio_spai       := round((n_sobrante - n_aio_spai), 2);
        end if;

        -- Actualiza el total de adelanto a interes
        n_total_io_iva_spai  := n_aio_spai + n_aivaio_spai;

        -- Busca si existe el folio adelanto, si no aperturalo
        select
        into   r_aux_ah * 
        from   auxiliares
        where  idorigen = r_mov.idorigen and idgrupo = r_mov.idgrupo and idsocio = r_mov.idsocio and
               idproducto = r_tab.dato2::integer;
        if not found then
          i_idorigenp  := r_mov.idorigen;
          i_idproducto := r_tab.dato2::integer;
          i_idauxiliar := sai_ahorro_crea_apertura(array[text(r_mov.idorigen), text(r_mov.idgrupo), text(r_mov.idsocio),
                                         text(r_mov.idorigen), r_tab.dato2, text(p_fecha), text(p_idusuario), '0.00']);
        else
          i_idorigenp  := r_aux_ah.idorigenp;
          i_idproducto := r_aux_ah.idproducto;
          i_idauxiliar := r_aux_ah.idauxiliar;
        end if;

        -- Inserta adelanto a interes
        raise notice '   > DESTINANDO AL ADELANTO A INTERES: %-%-%, IMPORTE DESTINADO: %',
                     i_idorigenp,i_idproducto,i_idauxiliar,n_total_io_iva_spai;
        t_ref_temporal := 'AI|'||r_mov.idorigenp::text||'|'||r_mov.idproducto::text||'|'||r_mov.idauxiliar::text||'|'||
                          n_aio_spai::text||'|'||n_aivaio_spai::text||'|'||split_part(t_adelanto,'|',4)::text;
        insert
        into   spei_entrada_temporal_cola_guardado
               (fecha_inserta, idusuario, sesion, referencia, idorigen, idgrupo, idsocio, idorigenp, idproducto,
                idauxiliar, esentrada, acapital, iva, tipo_amort, sai_aux, ref_temporal)
        values (r_mov.fecha_inserta, r_mov.idusuario, r_mov.sesion, r_mov.referencia, r_mov.idorigen, r_mov.idgrupo,
                r_mov.idsocio, i_idorigenp, i_idproducto, i_idauxiliar, TRUE, n_total_io_iva_spai, 0, 0, '',
                t_ref_temporal);

        -- Modifica el monto del prestamo, rebaja el adelanto a interes
        raise notice '   > POR EL ADELANTO A INTERES, EL IMPORTE ORIGINAL DEL CREDITO: % SE REDUJO A: %',
                     r_mov.acapital, r_mov.acapital - n_total_io_iva_spai;
        update spei_entrada_temporal_cola_guardado
        set    acapital = acapital - n_total_io_iva_spai,
               sai_aux = case when sai_aux is NULL or sai_aux = ''
                              then t_aux
                              else sai_aux
                         end
        where  fecha_inserta = r_mov.fecha_inserta and idusuario = r_mov.idusuario and sesion = r_mov.sesion and
               referencia = r_mov.referencia and idorigenp = r_mov.idorigenp and idproducto = r_mov.idproducto and
               idauxiliar = r_mov.idauxiliar;

        n_sobrante := n_sobrante - n_total_io_iva_spai;
      end if;

      -- Valida que el sobrante se vaya a saldo como adelanto a capital 
      if n_sobrante > (r_mov.saldo - (n_montoven + n_proxabono)) then
        n_sobrante := n_sobrante - (r_mov.saldo - (n_montoven + n_proxabono));
      else
        n_sobrante := 0.00;
      end if;

    end if;

    if n_sobrante > 0 then  -- Sobrante ajustalo al mismo folio del cargo
      if i_prod_resum > 0 then
        select
        into   r_paso *
        from   spei_entrada_temporal_cola_guardado
        where  idusuario = p_idusuario and sesion = p_sesion and date(fecha_inserta) = date(p_fecha) and
               referencia = p_referencia and not esentrada and idproducto = i_prod_resum;
        if found then
          insert
          into   spei_entrada_temporal_cola_guardado
                 (idusuario,sesion,fecha_inserta,referencia,idorigen,idgrupo,idsocio,idorigenp,idproducto,idauxiliar,
                  idx,idcuenta,esentrada,acapital,iva,tipo_amort)
          values (r_paso.idusuario,r_paso.sesion,r_paso.fecha_inserta,r_paso.referencia,r_paso.idorigen,r_paso.idgrupo,
                  r_paso.idsocio,r_paso.idorigenp,r_paso.idproducto,r_paso.idauxiliar,1,0,TRUE,n_sobrante,0,0);
        end if;
      else
        update spei_entrada_temporal_cola_guardado
        set    acapital = acapital - n_sobrante 
        where  idusuario = p_idusuario and sesion = p_sesion and date(fecha_inserta) = date(p_fecha) and
               referencia = p_referencia and not esentrada;
      end if;
    end if;
  end loop;

  return 1;
end;
$$ language 'plpgsql';

create or replace function
sai_spei_entrada_redistribuye_prestamo (date,integer,varchar,varchar) returns integer as $$
declare
  p_fecha               alias for $1;
  p_idusuario           alias for $2; 
  p_sesion              alias for $3;
  p_referencia          alias for $4;

  p_idorigen          integer;
  p_idgrupo           integer;
  p_idsocio           integer;
  p_idorigenp         integer;
  p_idproducto        integer;
  p_idauxiliar        integer;
  p_idcuenta          varchar(20);
  p_es                boolean;
  p_monto             numeric;
  p_tipoamort         integer;
  t_aux               text;
  p_dist              text;
  p_tipomov           integer;
  p_iva               numeric;
  p_tp                integer;
  p_ref_temporal      text;
  p_origen_mat        integer;
  paso_txt            text;
  x_acapital          numeric;
  x_io_pag            numeric;
  x_io_cal            numeric;
  x_im_pag            numeric;
  x_im_cal            numeric;
  x_ivaio_pag         numeric;
  x_ivaio_cal         numeric;
  x_ivaim_pag         numeric;
  x_ivaim_cal         numeric;
  x_cmnpag_pag        numeric;
  x_cmnpag_cal        numeric;
  x_montoseg          numeric;
  x_ret               numeric;
  x_saldodiacum       numeric;
  x_t_ivaio           numeric;
  x_t_ivaim           numeric;
  x_iva               numeric;
  x_t_iva             numeric;
  r_paso              record;
  r_mov               record;
  folio               integer;
  impte_desglo        numeric;
  cta_reembolso       varchar(20);
  x_prod_ret          integer;
  i_tipo_dist         integer;
  t_dim               _text;
  io_x                numeric;
  x_acapital2         numeric;
  monto_fijo          numeric;
  x                   integer;
  y                   integer;
  pagos_adelantados   boolean;
  x_diasven           integer;
  x_monto_com         numeric;
  x_seguro_hip        numeric;
  x_cual_com          integer;
  x_aseg_hip          numeric;
  x_acomision         numeric;
  paso_fecha          date;
  r_amort             record;
  i_idorigena         integer;
  x_diasvencidos      integer;
  x_montovencido      numeric;
  r_aux               record;
  b_sorteo_activado   boolean;
  r_tab               record;

begin

  x_io_pag        := 0;
  x_io_cal        := 0;
  x_im_pag        := 0;
  x_im_cal        := 0;
  x_ivaio_pag     := 0;
  x_ivaio_cal     := 0;
  x_ivaim_pag     := 0;
  x_ivaim_cal     := 0;
  x_montoseg      := 0;
  x_cmnpag_pag    := 0;
  x_cmnpag_cal    := 0;
  x_saldodiacum   := 0;
  x_ret           := 0;
  x_iva           := 0;
  x_diasvencidos  := 0;
  x_montovencido  := 0.00;


  x_t_ivaio := sai_iva_segun_sucursal(p_idorigenp,p_idproducto,0);
  x_t_ivaim := sai_iva_segun_sucursal(p_idorigenp,p_idproducto,1);
  x_t_iva   := x_t_ivaio;
  
  select
  into   r_aux *
  from   auxiliares
  where  (idorigenp,idproducto,idauxiliar) = (p_idorigenp,p_idproducto,p_idauxiliar);

  for r_mov
  in  select   mca.*, a.tipoamortizacion as ta, a.tasaio, a.idorigen, a.idgrupo, a.idsocio, a.saldo
      from     spei_entrada_temporal_cola_guardado mca
               inner join auxiliares a using(idorigenp,idproducto,idauxiliar)
               inner join productos  p using(idproducto)
      where    mca.fecha_inserta::date = p_fecha and mca.idusuario = p_idusuario and mca.sesion = p_sesion and
               mca.referencia = p_referencia and mca.idorigenp > 0 and mca.idproducto > 0 and mca.idauxiliar > 0 and
               mca.esentrada and p.tipoproducto = 2  --(esentrada = cargoabono = 1)
      order by esentrada
  loop
    x_acapital      := r_mov.acapital;
    p_idorigenp     := r_mov.idorigenp;
    p_idproducto    := r_mov.idproducto;
    p_idauxiliar    := r_mov.idauxiliar;
    p_origen_mat    := substr(p_idorigenp::text,1,3)||'00';
    p_ref_temporal  := r_mov.referencia;

    if r_mov.sai_aux is NULL or r_mov.sai_aux = '' then
      t_aux := sai_auxiliar(r_mov.idorigenp, r_mov.idproducto, r_mov.idauxiliar, p_fecha);
    else
      t_aux := r_mov.sai_aux;
    end if;

    x_io_cal      := split_part(t_aux,'|',7)::numeric;
    x_im_cal      := split_part(t_aux,'|',16)::numeric;
    x_ivaio_cal   := split_part(t_aux,'|',18)::numeric;
    x_ivaim_cal   := split_part(t_aux,'|',19)::numeric;
    x_io_pag      := split_part(t_aux,'|',7)::numeric;
    x_im_pag      := split_part(t_aux,'|',16)::numeric;
    x_ivaio_pag   := split_part(t_aux,'|',18)::numeric;
    x_ivaim_pag   := split_part(t_aux,'|',19)::numeric;

    if (x_im_pag + x_ivaim_pag) > x_acapital then
      x_im_pag    := x_acapital / ((x_t_ivaim / 100) + 1);
      x_ivaim_pag := x_acapital - x_im_pag;
      x_acapital  := 0;
    else
      x_acapital  := x_acapital - (x_im_pag + x_ivaim_pag);
    end if;

    if x_acapital > 0 then

      i_tipo_dist := 0;
      if r_aux.tipoamortizacion = 5 then
        select
        into   i_tipo_dist int4(dato1)
        from   tablas
        where  lower(idtabla) = 'param' and
               lower(idelemento) = 'distribuye_abonos_hipotecarios';
        if not found then
          i_tipo_dist := 0;
        end if;
        if i_tipo_dist is null then i_tipo_dist := 0; end if;
      end if;

      -- PARA LAS AMORTIZACIONES HIPOTECARIAS, EL MONTO DEBE DISTRIBUIRSE --
      -- PRIMERO EN UN SOLO PAGO Y LUEGO EN LOS SIGUIENTES -----------------
      if i_tipo_dist = 1 then

        x_acapital2 := 0;
        x_io_pag    := 0;
        x_ivaio_pag := 0;
        monto_fijo  := 0;

        if r_aux.tipoamortizacion = 5 then
          select
          into   monto_fijo monto
          from   monto_pagos_fijos
          where  idorigenp = p_idorigenp and idproducto = p_idproducto and idauxiliar = p_idauxiliar;
          if not found or monto_fijo is NULL then
            monto_fijo := 0;
          end if;
        end if;

        if monto_fijo > 0 then
          if no_usar_monto_pagos_fijos(p_idorigenp, p_idproducto, p_idauxiliar, monto_fijo, x_t_ivaio) then
            monto_fijo := 0;
          end if;
        end if;

        -- HAY QUE BUSCAR SI EL PRESTAMO TIENE PAGOS ADELANTADOS PARA EVITAR
        -- QUE SE COBREN INTERESES EN UN PAGO ADELANTADO, SOLO DEBE ABONARSE
        -- A CAPITAL -------------------------------------------------------
        pagos_adelantados := FALSE;
        for r_amort
        in  select   a1.vence, a1.abono, a1.io, idorigenp, idproducto, idauxiliar,
                     a1.abono - a1.abonopag as abono_a_pag,
                     a1.io - a1.iopag       as io_a_pag,
                     case when idamortizacion > 1 and monto_fijo > 0
                           then round(((monto_fijo - a1.abono - a1.io) - a1.iopag * (x_t_ivaio / 100)), 2)
                           else round(((a1.io - a1.iopag) * (x_t_ivaio / 100)), 2)
                     end                    as iva_io_a_pag
            from     amortizaciones as a1
            where    a1.idorigenp  = p_idorigenp and a1.idproducto = p_idproducto and a1.idauxiliar = p_idauxiliar and
                     a1.abono != a1.abonopag and todopag = FALSE
            order by vence
        loop
          if x_acapital > 0 then
            -- Si el prestamo ya tiene PAGOS ADELANTADOS, no se ---
            -- considera el IO ni su IVA de los proximos pagos ----
            if r_amort.vence > p_fecha and not pagos_adelantados then
              select
              into     paso_fecha vence
              from     amortizaciones
              where    (idorigenp,idproducto,idauxiliar) = (r_amort.idorigenp,r_amort.idproducto,r_amort.idauxiliar) and
                       vence < r_amort.vence
              order by vence desc
              limit    1;
              if not found then
                pagos_adelantados := FALSE;
                if p_fecha = r_aux.fechaactivacion and x_acapital = r_aux.saldo then
                  x_acapital2 := x_acapital;
                  pagos_adelantados := TRUE;
                  exit;
                end if;
              else
                pagos_adelantados := (p_fecha > paso_fecha and p_fecha <= r_amort.vence) = FALSE;
              end if;
            end if;

            if not pagos_adelantados then
              if x_acapital > (r_amort.io_a_pag + r_amort.iva_io_a_pag) then
                x_io_pag    := x_io_pag     + r_amort.io_a_pag;
                x_ivaio_pag := x_ivaio_pag  + r_amort.iva_io_a_pag;
                x_acapital  := x_acapital   - (r_amort.io_a_pag + r_amort.iva_io_a_pag);
              else
                io_x        := round((x_acapital / (1 + (x_t_ivaio / 100))), 2);
                x_io_pag    := x_io_pag    + io_x;
                x_ivaio_pag := x_ivaio_pag + (x_acapital - io_x);
                x_acapital  := 0;
              end if;
            end if;

            if x_acapital > 0 then
              if x_acapital >= r_amort.abono_a_pag then
                x_acapital2 := x_acapital2 + r_amort.abono_a_pag;
                x_acapital  := x_acapital  - r_amort.abono_a_pag;
              else
                exit when pagos_adelantados;
                x_acapital2 := x_acapital2 + x_acapital;
                x_acapital  := 0;
              end if;
            end if;

            exit when x_acapital <= 0;
          end if;
        end loop;

        -- SI LA DIFERENCIA DESPUES DE DISTRIBUIR EL PAGO ES MENOR O ---
        -- IGUAL A 0.02, SE AJUSTA CON EL IVA DEL IO, PERO SE DEBE -----
        -- CONSIDERAR TAMBIEN SI HAY IVA, Y QUE SI DEBE RESTARSE LA ----
        -- DIFERENCIA EL IVA SEA IGUAL O MAYOR A 0.02 ------------------
        -- (JFPA, 12/FEBRERO/2016) -------------------------------------
        if x_acapital != 0 and abs(x_acapital) <= 0.02 and x_ivaio_pag > 0 then
          if x_acapital < 0 then
            if x_ivaio_pag >= 0.02 then
              x_ivaio_pag := x_ivaio_pag + x_acapital;
              x_acapital := 0.0;
            end if;
          else
            x_ivaio_pag := x_ivaio_pag + x_acapital;
            x_acapital := 0.0;
          end if;
        end if;

        x_acapital := x_acapital2;

      else
        if (x_io_pag + x_ivaio_pag) > x_acapital then
          x_io_pag    := round(x_acapital / ((x_t_ivaio / 100) + 1), 2);
          x_ivaio_pag := x_acapital - x_io_pag;
          x_acapital  := 0;
        else
          x_acapital  := x_acapital - (x_io_pag + x_ivaio_pag);
          x_acapital  := case when x_acapital <= 0 then 0 else x_acapital end;
        end if;
      end if;
    else
      x_io_pag    := 0.0;
      x_ivaio_pag := 0.0;
    end if;

    x_diasvencidos := split_part(t_aux,'|',4)::numeric;
    x_montovencido := split_part(t_aux,'|',5)::numeric;


    if x_acapital > 0 or x_io_pag > 0 or x_im_pag > 0 or x_cmnpag_pag > 0 then
---- ******************************************************************
----  SORTEO ELECTRONICO SAGRADA ---
----  Estatus del prestamo antes de
      if p_origen_mat = 20700 then --and p_tp = 2 then --- Caja Sagrada La Familia
      
        b_sorteo_activado := FALSE;
        select
        into   r_tab *
        from   tablas
        where  idtabla = 'sorteo_electronico' and idelemento = 'productos';
        if found then
          b_sorteo_activado := TRUE;
          
          if r_tab.dato1::integer != 1 then
            raise notice 'sorteo electronico: desactivado (no existe tabla o switch 0)';
            b_sorteo_activado := FALSE;
          end if;
          if r_tab.dato4 is not NULL and r_tab.dato4 != '' and r_tab.dato4::date > p_fecha then
            raise notice 'sorteo electronico: comienza a partir del %',r_tab.dato4;
            b_sorteo_activado := FALSE;
          end if;
          if r_tab.dato5 is not null and r_tab.dato5 != '' and r_tab.dato5::integer != 1 then
            raise notice 'sorteo electronico: desactivado para banca movil';
            b_sorteo_activado := FALSE;
          end if;
        end if;
        if b_sorteo_activado then
          if p_ref_temporal is NULL or p_ref_temporal = '' then
            select
            into   x coalesce(count(*),0)
            from   amortizaciones
            where  (idorigenp,idproducto,idauxiliar) = (p_idorigenp,p_idproducto,p_idauxiliar) and todopag;
  
            p_ref_temporal := x::text||'|'||sai_token(14,t_aux,'|');
          else
            raise notice '*** ERROR AFECTA A SORTEO ELECTRONICO PARA BANCA MOVIL : *** '
                         '  NO GUARDO INFORMACION DEL PRESTAMO EN "temporal.referencia" YA QUE ESTE CAMPO NO ESTABA VACIO,'
                         '  POR LO TANTO ESTE MOVIMIENTO DE PRESTAMO NO SE TOMARA EN CUENTA PARA SORTEO ELECTRONICO';
          end if;
        end if;
      end if;

---- ******************************************************************

      update spei_entrada_temporal_cola_guardado
      set    acapital = x_acapital, io_pag = x_io_pag, io_cal = x_io_cal, im_pag = x_im_pag, im_cal = x_im_cal,
             ivaio_pag = x_ivaio_pag, ivaio_cal = x_ivaio_cal, ivaim_pag = x_ivaim_pag, ivaim_cal = x_ivaim_cal,
             diasvencidos = x_diasvencidos, montovencido = x_montovencido, sai_aux = t_aux
      where  fecha_inserta = r_mov.fecha_inserta and idusuario = p_idusuario and sesion = p_sesion and
             referencia = p_referencia and
             (idorigenp,idproducto,idauxiliar) = (r_mov.idorigenp,r_mov.idproducto,r_mov.idauxiliar);

---- ******************************************************************

    end if;
  end loop;

  return 1;
end;
$$ language 'plpgsql';


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
     where  idusuario = p_idusuario and sesion = p_sesion);

  t_periodo := trim(to_char(d_fecha_hoy,'yyyymm'));

  i_idpoliza = sai_folio(TRUE,'pol'||t_periodo||trim(to_char(i_idorigen_ap,'099999'))||i_tp_pol::text);

  lock table usuarios in row exclusive mode;

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

  update spei_entrada_temporal_cola_guardado
  set    poliza_generada = t_poliza_generada, aplicado = TRUE, fecha_aplicado = d_fecha_hoy + clock_timestamp()::time
  where  idusuario = p_idusuario and sesion = p_sesion;

  -- DELETE FROM al "termporal"
  DELETE FROM temporal WHERE idusuario = p_idusuario AND sesion = p_sesion;
  --  NOTA: Wilmer manipula en su codigo...

  query_delete := 'DELETE FROM spei_entrada_temporal_cola_guardado WHERE sesion = '||p_sesion||' AND idusuario ='||p_idusuario||' AND referencia ='||p_referencia||' AND idoperacion ='||p_idop;

  update spei_entrada_temporal_cola_guardado
  set query = query_delete
  WHERE sesion = p_sesion AND idusuario = p_idusuario AND referencia = p_referencia and idoperacion = p_idop::bigint;

  DELETE FROM spei_entrada_temporal_cola_guardado
  WHERE sesion = p_sesion AND idusuario = p_idusuario AND referencia = p_referencia AND idoperacion = p_idop::bigint;

  return i_movs;
end;
$$ language 'plpgsql';

-- ESTA FUNCION SE USA EN SAICOOP, (LO PIDIO MITRAS)-----
create or replace function sai_asigna_clabe_por_socio(integer, integer, integer)
returns varchar as $$
declare
  p_idorigen alias for $1;
  p_idgrupo  alias for $2;
  p_idsocio  alias for $3;

  clabex varchar;
  clabey varchar;
begin

  clabex := '6461805187'||substr(trim(to_char(p_idorigen, '099999')),5,2)||trim(to_char(p_idsocio,'09999'));
  clabey := NULL;

  select into clabey clabe
  from ws_siscoop_clabe
  where clabe like clabex||'%' and not seleccionada and not asignada and not eliminada;
  if not found or clabey is NULL then return ''; end if;

  if length(clabey) < 18 then return ''; end if;

  return clabey;

  ------------------------------------------------------------------------------
  -- ULTIMA MODIFICACION : 03/JUNIO/2024 ---------------------------------------
  ------------------------------------------------------------------------------
end;
$$ language 'plpgsql';

