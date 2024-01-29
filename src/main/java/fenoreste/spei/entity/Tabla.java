/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fenoreste.spei.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Wilmer
 */
@Entity
@Table(name = "tablas")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tabla implements Serializable {    
	
	@EmbeddedId
	private TablaPK tablasPK;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "dato1")
    private String dato1;
    @Column(name = "dato2")
    private String dato2;
    @Column(name = "dato3")
    private String dato3;
    @Column(name = "dato4")
    private String dato4;
    @Column(name = "dato5")
    private String dato5;
    @Column(name = "tipo")
    private short tipo;

	private static final long serialVersionUID = 1L;

}
