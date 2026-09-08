package fenoreste.spei.service;

import fenoreste.spei.dao.AmortizacionDao;
import fenoreste.spei.entity.Amortizacion;
import fenoreste.spei.entity.AuxiliarPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AmortizacionServiceImpl implements IAmortizacionService{

    @Autowired
    private AmortizacionDao amortizacionDao;

    @Override
    public Amortizacion buscarSiguienteAmortizacion(AuxiliarPK auxiliarPK) {
        return amortizacionDao.buscarSiguienteAmortizacion(auxiliarPK.getIdorigenp(),auxiliarPK.getIdproducto(),auxiliarPK.getIdauxiliar());
    }
}
