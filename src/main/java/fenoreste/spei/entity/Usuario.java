package fenoreste.spei.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements Serializable {
     @Id
	 private Integer                idusuario      ;
	 private String                 login          ;
	 private String                 nombre         ;
	 private boolean                activo         ;
	 private Integer                idorigenp_dd   ;
	 private Integer                idproducto_dd  ;
	 private Integer                idauxiliar_dd  ;
	 private Integer                idorigenp_ad   ;
	 private Integer                idproducto_ad  ;
	 private Integer                idauxiliar_ad  ;
	 private String                 pingreso       ;
	 private String                 pegreso        ;
	 private String                 pdiario        ;
	 private Integer                idorigen       ;
	 private Integer                p_idorigen     ;
	 private Integer                p_idgrupo      ;
	 private Integer                p_idsocio      ;
	 private String                 impticket      ;
	 private String                 passwd         ;
	 private String                 ip_local       ;
	 private Integer                ticket         ;
	 
	 private static final long serialVersionUID = 1L;
}
