package fenoreste.spei.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TablaPK implements Serializable{
	@Column(name="idtabla", nullable=false)
	private String idTabla;
	@Column(name="idelemento",nullable = false)
	private String idElemento;
	
	private static final long serialVersionUID = 1L;

}
