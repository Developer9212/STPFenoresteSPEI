package fenoreste.spei.service;

import java.util.List;

import fenoreste.spei.entity.AbonoSpei;

public interface IAbonoSpeiService {
    
	public AbonoSpei buscarPorId(Integer id);
	public List<AbonoSpei>todasPorFecha(Integer fecha);

	public void guardar(AbonoSpei abono);
}
