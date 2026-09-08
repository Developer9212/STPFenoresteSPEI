package fenoreste.spei.service;

import org.springframework.stereotype.Service;

import fenoreste.spei.entity.Origen;



@Service
public interface IOrigenService {

	public Origen buscarPorId(Integer id);
	public Origen buscarMatriz();
	
	
}
