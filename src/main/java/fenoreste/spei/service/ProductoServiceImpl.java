package fenoreste.spei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.ProductoDao;
import fenoreste.spei.entity.Producto;

@Service
public class ProductoServiceImpl implements IProductoService{
    
	@Autowired
	private ProductoDao productoDao; 
	
	@Override
	public Producto buscarPorId(Integer id) {
		return productoDao.findById(id).orElse(null);
	}

}
