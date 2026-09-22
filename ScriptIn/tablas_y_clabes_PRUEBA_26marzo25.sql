--- CONTIENE LA CLAVE INTERBANCARIA ANTES DE ASIGNARLAS
--- DROP TABLE IF EXISTS ws_siscoop_clabe CASCADE;
CREATE TABLE ws_siscoop_clabe (
  fecha              DATE NOT NULL,
  clabe              VARCHAR(18) NOT NULL,
  seleccionada       BOOLEAN NOT NULL DEFAULT FALSE,
  asignada           BOOLEAN NOT NULL DEFAULT FALSE,
  eliminada          BOOLEAN NOT NULL DEFAULT FALSE,
  fecha_vencimiento  DATE,
  CONSTRAINT ws_siscoop_clabe_pkey PRIMARY KEY (clabe)
);
COMMENT ON TABLE ws_siscoop_clabe IS 'Contiene las clabes interbancarias';


--- CONTIENE LA CLAVE INTERBANCARIA Y A SU RESPECTIO OPA
--- DROP TABLE IF EXISTS ws_siscoop_clabe_interbancaria CASCADE;
CREATE TABLE ws_siscoop_clabe_interbancaria (
  idorigenp     INTEGER NOT NULL,
  idproducto    INTEGER NOT NULL,
  idauxiliar    INTEGER NOT NULL,
  clabe         VARCHAR(18) NOT NULL,
  fecha_hora    TIMESTAMP WITH TIME ZONE NOT NULL,
  asignada      BOOLEAN NOT NULL DEFAULT FALSE,
  activa        BOOLEAN NOT NULL DEFAULT FALSE,
  bloqueada     BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT ws_siscoop_clabe_interbancaria_pkey PRIMARY KEY (idorigenp, idproducto, idauxiliar),
  CONSTRAINT opa_fkey FOREIGN KEY (idorigenp, idproducto, idauxiliar)
      REFERENCES auxiliares (idorigenp, idproducto, idauxiliar) MATCH SIMPLE,
  CONSTRAINT clabe_fkey FOREIGN KEY (clabe)
      REFERENCES ws_siscoop_clabe (clabe) MATCH SIMPLE
);
COMMENT ON TABLE ws_siscoop_clabe_interbancaria IS 'Contiene las clabes interbancarias de los opas';


--- CONTIENE EL HISTORIAL DE LA CLAVE INTERBANCARIA Y A SU RESPECTIO OPA
--- DROP TABLE IF EXISTS ws_siscoop_clabe_interbancaria_h CASCADE;
CREATE TABLE ws_siscoop_clabe_interbancaria_h (
  idorigenp     INTEGER NOT NULL,
  idproducto    INTEGER NOT NULL,
  idauxiliar    INTEGER NOT NULL,
  clabe         VARCHAR(18) NOT NULL,
  fecha_hora    TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT ws_siscoop_clabe_interbancaria_h_pkey PRIMARY KEY (idorigenp, idproducto, idauxiliar, clabe),
  CONSTRAINT clabe_h_fkey FOREIGN KEY (idorigenp, idproducto, idauxiliar)
      REFERENCES ws_siscoop_clabe_interbancaria (idorigenp, idproducto, idauxiliar) MATCH SIMPLE
);
COMMENT ON TABLE ws_siscoop_clabe_interbancaria_h IS 'Contiene el historial de las clabes interbancarias de los opas';

-- insert into tablas values ('param', 'productos_para_cuenta_clabe', NULL, NULL, '133', NULL, NULL, NULL, 0);

-- DATOS DE PRUEBA ---

insert into ws_siscoop_clabe values (date('06/03/2025'), '646180110400000007',FALSE, FALSE, FALSE, date('06/03/2040'));
insert into ws_siscoop_clabe values (date('06/03/2025'), '646180206800000003',FALSE, FALSE, FALSE, date('06/03/2040'));
insert into ws_siscoop_clabe values (date('06/03/2025'), '646180209100000014',FALSE, FALSE, FALSE, date('06/03/2040'));


--- FOLIO DE PRUEBA
insert into ws_siscoop_clabe_interbancaria values (30512,30303,1474, '646180110400000007', now(), TRUE, TRUE, FALSE);

insert into tablas values ('param', 'asignacion_individual_de_clabes', NULL, '1', '2', NULL, NULL, NULL, 0);


/*
select * from auxiliares
where (idorigenp,idproducto,idauxiliar) in
      (select idorigenp,idproducto,idauxiliar
       from ws_siscoop_clabe_interbancaria);
        
select * from amortizaciones
where (idorigenp,idproducto,idauxiliar) in
      (select idorigenp,idproducto,idauxiliar
       from ws_siscoop_clabe_interbancaria)
order by idorigenp,idproducto,idauxiliar,idamortizacion;

       from ws_siscoop_clabe_interbancaria);
 idorigen  idgrupo  idsocio  idorigenp  idproducto  idauxiliar              CLABE
    30501       10   117392      30512       30303        1474 646180110400000007
*/

