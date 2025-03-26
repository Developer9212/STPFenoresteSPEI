package fenoreste.spei.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fenoreste.spei.entity.Auxiliar;
import fenoreste.spei.entity.AuxiliarPK;

import java.util.Date;


public interface FuncionDao extends JpaRepository<Auxiliar,AuxiliarPK> {

	@Query(value="SELECT spei_entrada_servicio_activo_inactivo()",nativeQuery = true)
	public boolean activo();
	
	@Query(value = "SELECT text(pg_backend_pid())||'-'||trim(to_char(now(),'ddmmyy'))" , nativeQuery = true)
	public String sesion();
	
	@Query(value = "SELECT sai_spei_entrada_aplica(?1,?2,?3,?4,?5)", nativeQuery = true)
	public Integer movs_aplicados(Integer idusuario,String sesion,Integer tipopoliza,String referencia,String idop);
	
	@Query(value = "SELECT sai_auxiliar(?,?,?,(SELECT date(fechatrabajo) FROM origenes LIMIT 1))" , nativeQuery = true)
	public String sai_auxiliar(Integer idorigenp,Integer idproducto,Integer idauxiliar);

	@Query(value = "SELECT now()" , nativeQuery = true)
	public String dateServidorBase();
	
	@Query(value = "DELETE FROM temporal WHERE idusuario =?1 AND sesion=?2", nativeQuery = true)
	public int EliminarTemporal(Integer idusuario,String sesion);

	@Query(value="SELECT sai_bankingly_prestamo_cuanto(?1,?2,?3,?4,?5,?6)", nativeQuery=true)
	public String sai_prestamo_cuanto(Integer idorigenp, Integer idproducto, Integer idauxiliar, Date fecha, Integer tipoamortizacion, String sai);

	

}

