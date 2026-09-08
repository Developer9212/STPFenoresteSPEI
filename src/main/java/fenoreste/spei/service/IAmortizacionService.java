package fenoreste.spei.service;

import fenoreste.spei.entity.Amortizacion;
import fenoreste.spei.entity.AuxiliarPK;

public interface IAmortizacionService {

    public Amortizacion buscarSiguienteAmortizacion(AuxiliarPK auxiliarPK);
}
