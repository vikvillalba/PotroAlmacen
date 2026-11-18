/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.salidas.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Tabla {@code sal_boleta_detalle}: una línea (material y cantidad) de una boleta.
 *
 * <p>El material es del módulo Materiales, por eso solo se guarda su código y el nombre que tenía al entregarlo,
 * sin FK ni relación JPA hacia {@code mat_material}.
 */
@Entity
@Table(name = "sal_boleta_detalle")
public class BoletaDetalleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boleta_id", nullable = false)
    private BoletaEntity boleta;

    @Column(name = "codigo_material", nullable = false, length = 50)
    private String codigoMaterial;

    @Column(name = "nombre_material", length = 500)
    private String nombreMaterial;

    @Column(nullable = false)
    private Integer cantidad;

    protected BoletaDetalleEntity() {
        // Requerido por JPA.
    }

    public Long getId() {
        return id;
    }

    public BoletaEntity getBoleta() {
        return boleta;
    }

    void setBoleta(BoletaEntity boleta) {
        this.boleta = boleta;
    }

    public String getCodigoMaterial() {
        return codigoMaterial;
    }

    public void setCodigoMaterial(String codigoMaterial) {
        this.codigoMaterial = codigoMaterial;
    }

    public String getNombreMaterial() {
        return nombreMaterial;
    }

    public void setNombreMaterial(String nombreMaterial) {
        this.nombreMaterial = nombreMaterial;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
