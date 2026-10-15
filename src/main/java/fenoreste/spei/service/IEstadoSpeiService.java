package fenoreste.spei.service;

import fenoreste.spei.entity.EstadoSpei;

public interface IEstadoSpeiService {

    public EstadoSpei buscarPorId(Integer id);
    public void guardar(EstadoSpei estadoSpei);
}
