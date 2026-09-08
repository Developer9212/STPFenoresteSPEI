package fenoreste.spei.service;

import fenoreste.spei.entity.AuxiliarPK;

import java.util.Date;

public interface IFuncionesSaiService {
 
	public boolean horario_actividad();
    public String session();	
    public Integer aplica_movs(Integer idusuario,String sesion,Integer tipopoliza,String referencia,String idop);
    public String sai_auxiliar(AuxiliarPK pk);
    public Date dateServidorBase();
    public void eliminaTemporal(Integer idusuario,String sesion);
    public String sai_bankingly_prestamo_cuanto(AuxiliarPK opa,Date fecha,Integer tipoamortizacion,String saiAuxiliar);
}
