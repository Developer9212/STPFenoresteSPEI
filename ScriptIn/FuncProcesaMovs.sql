create or replace function
sai_spei_entrada_aplica (integer, text,integer,text,text) returns integer as $$
declare
  p_idusuario         alias for $1;
  p_sesion            alias for $2;
  p_tipopoliza        alias for $3;
  p_referencia        alias for $4;
  p_idop  		      alias for $5;/*Se añade el 13/12/2024 Wilmer para una eliminacion segura se obtiene error cargos ! de abonos*/
 

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


  /*Añadido el 11/12/2024-- Wilmer*/

  t_movs_ref  integer;
begin

  -- Tipo poliza:
  i_tp_pol := p_tipopoliza;
/*
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
*/
  -- SE HIZO UNA CORRECCION EN LOS CAMPOS io_cal Y saldodiacum PORQUE SE ESTABAN
  -- DEJANDO EN CERO PERO ESO CAUSABA UN ERROR A FIN DE MES, EN EL PAGO DEL
  -- INTERES AL AHORRO, YA QUE SE MOVIA LA fechaumi DEL AUXILIAR Y SE ELIMINABA
  -- LO QUE SE TENIA GUARDADO EN EL CAMPO DE INTERES (JFPA, 25/ABRIL/2024)

  LOCK  TABLE spei_entrada_temporal_cola_guardado IN ROW EXCLUSIVE MODE;

  t_movs_ref := (SELECT count(*) FROM polizas WHERE concepto LIKE '%SPEI ENTRADA%' || p_referencia || '%'
				 AND idusuario = (SELECT dato1 FROM tablas WHERE idtabla='spei_entrada' AND idelemento ='usuario')::integer);

    -- Verifica si el conteo es menor o igual a 0 
    IF t_movs_ref <= 0 THEN
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
   
   query_delete := 'DELETE FROM spei_entrada_temporal_cola_guardado WHERE sesion = '||p_sesion||' AND idusuario ='||p_idusuario||' AND referencia ='||p_referencia||' AND idoperacion ='||p_idop;
			  
   update spei_entrada_temporal_cola_guardado set query = query_delete WHERE sesion = p_sesion 
              AND idusuario = p_idusuario AND referencia = p_referencia and idoperacion = p_idop;
			  
   DELETE FROM spei_entrada_temporal_cola_guardado WHERE sesion = p_sesion 
              AND idusuario = p_idusuario AND referencia = p_referencia AND 
			  idoperacion = p_idop;
  ELSE 
    
	i_movs := -1001;/*Modificado por Wilmer duplicidad de abonos en CSN*/
    
  END IF;

  
  return i_movs;
end;
$$ language 'plpgsql';
