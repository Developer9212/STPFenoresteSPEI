package fenoreste.spei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import fenoreste.spei.dao.FuncionDao;
import fenoreste.spei.entity.AuxiliarPK;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.transaction.Transactional;

@Service
public class FuncionesSaiServiceImpl implements IFuncionesSaiService {
    
	@Autowired
	private FuncionDao funcionesDao;
	
	@Override
	public boolean horario_actividad() {		
		return funcionesDao.activo();
	}

	@Override
	public String session() {
		return funcionesDao.sesion();
	}

	@Override
	@Transactional
	public Integer aplica_movs(Integer idusuario, String sesion,Integer tipopoliza,String referencia,String idop) {
		return funcionesDao.movs_aplicados(idusuario, sesion,tipopoliza,referencia,idop);
	}

	@Override
	@Transactional
	public String sai_auxiliar(AuxiliarPK pk) {
		return funcionesDao.sai_auxiliar(pk.getIdorigenp(),pk.getIdproducto(),pk.getIdauxiliar());
	}

	@Override
	public Date dateServidorBase(){
       return dateServidor(funcionesDao.dateServidorBase());

	}


	private Date dateServidor(String fecha){
		Date date = new Date();
		fecha = fecha.substring(0, 19) + "-06:00"; // Convertir a formato manejable
		// Creamos el formato correspondiente
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssXXX");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Configuramos la zona horaria

		try {
			// Convertimos la cadena a Date
			date = sdf.parse(fecha);
			System.out.println("Fecha convertida: " + date);
		} catch (ParseException e) {
			System.out.println("Error al convertir fecha String: " + e.getMessage());
		}
		return date;
	}
     
	@Override
	@Transactional
	public void eliminaTemporal(Integer idusuario, String sesion) {
		funcionesDao.EliminarTemporal(idusuario, sesion);
	}

	@Override
	public String sai_spei_entrada_prestamo_cuanto(AuxiliarPK opa, Integer tipoamortizacion, String saiAuxiliar) {
		return funcionesDao.sai_prestamo_cuanto(opa.getIdorigenp(),
				                                opa.getIdproducto(),
				                                opa.getIdauxiliar(),
				                                tipoamortizacion,
												saiAuxiliar
		                          );
	}

	@Override
	public Double sai_spei_entrada_prestamo_adelanto_exacto(AuxiliarPK opa, Double monto) {
		return funcionesDao.prestamo_adelanto_exacto(opa.getIdorigenp(),opa.getIdproducto(),opa.getIdauxiliar(),new BigDecimal(monto));
	}


}
