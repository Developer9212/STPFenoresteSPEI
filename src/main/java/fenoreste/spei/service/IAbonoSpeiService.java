package fenoreste.spei.service;

import java.util.List;

import fenoreste.spei.entity.AbonoSpei;
import fenoreste.spei.entity.AbonoSpeiDuplicado;
import fenoreste.spei.entity.AbonoSpeiPK;

public interface IAbonoSpeiService {
    
	public AbonoSpei buscarPorId(AbonoSpeiPK id);
	public Double montoDiario(Integer fecha,String cuenta);

	public void guardar(AbonoSpei abono);

	public Double totalMes(String clabe,String periodo);

	public void guardarDuplicado(AbonoSpeiDuplicado abono);
}
