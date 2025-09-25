package fenoreste.spei.service;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import fenoreste.spei.entity.SpeiTemporalPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.SpeiTemporalDao;
import fenoreste.spei.entity.SpeiTemporal;

@Service
public class SpeiTemporalServiceImpl implements ISpeiTemporalService {

    @Autowired
    private SpeiTemporalDao speiTemporalDao;

    @Autowired
    private EntityManager entityManager;


    @Override
    @Transactional
    @Modifying
    public void guardar(SpeiTemporal mov) {
        /*String sql = "INSERT INTO spei_entrada_temporal_cola_guardado (" +
                "idorigenp,idproducto,idauxiliar,idoperacion, referencia, aplicado, idusuario, sesion, idorigen, idgrupo, idsocio, " +
                "acapital, io_pag, io_cal, im_pag, im_cal, aiva, saldodiacum, abonifio, " +
                "idcuenta, ivaio_pag, ivaio_cal, ivaim_pag, ivaim_cal, mov, tipomov, " +
                "diasvencidos, montovencido, idorigena, huella_valida, concepto_mov, " +
                "fe_nom_archivo, fe_xml, sai_aux, poliza_generada, tipopoliza, esentrada" +
                ") VALUES (" +
                ":idorigenp, :idproducto, :idauxiliar, :idoperacion, :referencia, :aplicado, :idusuario, :sesion, :idorigen, :idgrupo, :idsocio, " +
                ":acapital, :io_pag, :io_cal, :im_pag, :im_cal, :aiva, :saldodiacum, :abonifio, " +
                ":idcuenta, :ivaio_pag, :ivaio_cal, :ivaim_pag, :ivaim_cal, :mov, :tipomov, " +
                ":diasvencidos, :montovencido, :idorigena, :huella_valida, :concepto_mov, " +
                ":fe_nom_archivo, :fe_xml, :sai_aux, :poliza_generada, :tipopoliza, :esentrada" +
                ") ON CONFLICT (idorigenp,idproducto,idauxiliar,idoperacion, referencia) DO NOTHING";*/


       String  sql = "INSERT INTO spei_entrada_temporal_cola_guardado (" +
                "idorigenp,idproducto,idauxiliar,idoperacion, referencia, aplicado, idusuario, sesion," +
                "idorigen, idgrupo, idsocio, acapital, io_pag, io_cal, im_pag, im_cal, aiva," +
                "saldodiacum, abonifio, idcuenta, ivaio_pag, ivaio_cal, ivaim_pag, ivaim_cal, mov," +
                "tipomov, diasvencidos, montovencido, idorigena, huella_valida, concepto_mov," +
                "fe_nom_archivo, fe_xml, sai_aux, poliza_generada, tipopoliza, esentrada)" +
                "SELECT :idorigenp, :idproducto, :idauxiliar, :idoperacion, :referencia, :aplicado, :idusuario, :sesion," +
                ":idorigen, :idgrupo, :idsocio, :acapital, :io_pag, :io_cal, :im_pag, :im_cal, :aiva, " +
                ":saldodiacum, :abonifio, :idcuenta, :ivaio_pag, :ivaio_cal, :ivaim_pag, :ivaim_cal, :mov," +
                ":tipomov, :diasvencidos, :montovencido, :idorigena, :huella_valida, :concepto_mov, " +
                ":fe_nom_archivo, :fe_xml, :sai_aux, :poliza_generada, :tipopoliza, :esentrada  WHERE NOT EXISTS (" +
                " SELECT 1" +
                "FROM spei_entrada_temporal_cola_guardado t" +
                " WHERE t.idorigenp   = :idorigenp" +
                " AND t.idproducto  = :idproducto" +
                " AND t.idauxiliar  = :idauxiliar" +
                " AND t.idoperacion = :idoperacion" +
                " AND t.referencia  = :referencia)";


        entityManager.createNativeQuery(sql)
                .setParameter("idorigenp", mov.getSpeiTemporalPK().getIdorigenp())
                .setParameter("idproducto", mov.getSpeiTemporalPK().getIdproducto())
                .setParameter("idauxiliar", mov.getSpeiTemporalPK().getIdauxiliar())
                .setParameter("idoperacion", mov.getSpeiTemporalPK().getIdoperacion()) // Ajusta según campos de PK
                .setParameter("referencia", mov.getSpeiTemporalPK().getReferencia())
                .setParameter("aplicado", mov.isAplicado())
                .setParameter("idusuario", mov.getIdusuario())
                .setParameter("sesion", mov.getSesion())
                .setParameter("idorigen", mov.getIdorigen())
                .setParameter("idgrupo", mov.getIdgrupo())
                .setParameter("idsocio", mov.getIdsocio())
                .setParameter("acapital", mov.getAcapital())
                .setParameter("io_pag", mov.getIo_pag())
                .setParameter("io_cal", mov.getIo_cal())
                .setParameter("im_pag", mov.getIm_pag())
                .setParameter("im_cal", mov.getIm_cal())
                .setParameter("aiva", mov.getAiva())
                .setParameter("saldodiacum", mov.getSaldodiacum())
                .setParameter("abonifio", mov.getAbonifio())
                .setParameter("idcuenta", mov.getIdcuenta())
                .setParameter("ivaio_pag", mov.getIvaio_pag())
                .setParameter("ivaio_cal", mov.getIvaio_cal())
                .setParameter("ivaim_pag", mov.getIvaim_pag())
                .setParameter("ivaim_cal", mov.getIvaim_cal())
                .setParameter("mov", mov.getMov())
                .setParameter("tipomov", mov.getTipomov())
                .setParameter("diasvencidos", mov.getDiasvencidos())
                .setParameter("montovencido", mov.getMontovencido())
                .setParameter("idorigena", mov.getIdorigena())
                .setParameter("huella_valida", mov.isHuella_valida())
                .setParameter("concepto_mov", mov.getConcepto_mov())
                .setParameter("fe_nom_archivo", mov.getFe_nom_archivo())
                .setParameter("fe_xml", mov.getFe_xml())
                .setParameter("sai_aux", mov.getSai_aux())
                .setParameter("poliza_generada", mov.getPoliza_generada())

                .setParameter("tipopoliza", mov.getTipopoliza())
                .setParameter("esentrada", mov.isEsentrada())
                .executeUpdate();
    }


    @Override
    @Transactional
    @Modifying
    public void eliminar(String sesion, String referencia) {
        int eliminado = 0;//speiTemporalDao.eliminarRegistro(sesion, referencia);
    }

    @Override
    @Transactional
    public void eliminarTodos() {
        speiTemporalDao.deleteAll();
    }

    @Override
    public SpeiTemporal buscarPorId(SpeiTemporalPK pk) {
        return speiTemporalDao.findById(pk).orElse(null);
    }

    @Override
    public Integer totalTemporales(Integer idorigen, Integer idgrupo, Integer idsocio, String referecia, double acapital) {
        List<SpeiTemporal> lista = speiTemporalDao.todasEnTemporal(idorigen, idgrupo, idsocio, referecia, acapital);
        return lista.size();
    }


}
