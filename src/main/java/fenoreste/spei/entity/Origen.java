/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fenoreste.spei.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author wilmer
 */

@Entity
@Table(name = "origenes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Origen implements Serializable{
    @Id
    @Column(name = "idorigen")
    private Integer idorigen;
    @Column(name = "matriz")
    private int matriz;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "calle")
    private String calle;
    @Column(name = "numeroext")
    private String numeroext;
    @Column(name = "numeroint")
    private String numeroint;
    @Column(name = "telefono1")
    private String telefono1;
    @Column(name = "telefono2")
    private String telefono2;
    @Column(name = "codigopostal")
    private String codigopostal;
    @Column(name = "estatus")
    private boolean estatus;
    @Column(name = "fechatrabajo")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechatrabajo;
    @Column(name = "cta_rendimientos")
    private String ctaRendimientos;
    @Column(name = "cta_quebrantos")
    private String ctaQuebrantos;
    @Column(name = "cta_efectivo")
    private String ctaEfectivo;
    @Column(name = "cta_documentos1")
    private String ctaDocumentos1;
    @Column(name = "cta_documentos2")
    private String ctaDocumentos2;
    @Column(name = "cta_documentos3")
    private String ctaDocumentos3;
    @Column(name = "cta_documentos4")
    private String ctaDocumentos4;
    @Column(name = "cta_documentos5")
    private String ctaDocumentos5;
    @Column(name = "enlinea")
    private boolean enlinea;
    @Column(name = "cta_documentos6")
    private String ctaDocumentos6;
    @Column(name = "cta_documentos7")
    private String ctaDocumentos7;
    
    private final static long serialVersionUID = 1L;    
}
