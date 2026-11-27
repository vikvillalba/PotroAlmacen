package mx.itson.potroalmacen.areas.adapter.out.persistence;

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
 * Tabla {@code are_area_material}: material permitido para un área.
 *
 * <p>El material es del módulo Materiales; solo se guarda su código y nombre, sin FK hacia {@code mat_material}.
 */
@Entity
@Table(name = "are_area_material")
public class AreaMaterialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private AreaEntity area;

    @Column(name = "codigo_material", nullable = false, length = 50)
    private String codigoMaterial;

    @Column(name = "nombre_material", length = 500)
    private String nombreMaterial;

    @Column(nullable = false)
    private int cantidad = 1;

    protected AreaMaterialEntity() {
        // Requerido por JPA.
    }

    public Long getId() {
        return id;
    }

    public AreaEntity getArea() {
        return area;
    }

    void setArea(AreaEntity area) {
        this.area = area;
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

    /** Cantidad máxima del material que se puede entregar al área en una solicitud. */
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
