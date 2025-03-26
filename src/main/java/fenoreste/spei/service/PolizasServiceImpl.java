package fenoreste.spei.service;

import fenoreste.spei.dao.PolizaDao;
import fenoreste.spei.entity.Poliza;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolizasServiceImpl implements IPolizaService{

    @Autowired
    private PolizaDao polizaDao;

    @Override
    public Poliza buscarPorConcepto(String concepto) {
        return null;
    }
}
