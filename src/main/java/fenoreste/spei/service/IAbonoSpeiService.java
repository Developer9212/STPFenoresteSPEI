package fenoreste.spei.service;

import java.util.List;

import fenoreste.spei.entity.AbonoSpei;

public interface IAbonoSpeiService {
    
	public AbonoSpei buscarPorId(Integer id);
	public Double montoDiario(Integer fecha,String cuenta);

	public void guardar(AbonoSpei abono);

	public Double totalMes(String clabe,String periodo);
}
