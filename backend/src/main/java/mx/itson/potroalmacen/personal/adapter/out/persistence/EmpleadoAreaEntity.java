package mx.itson.potroalmacen.personal.adapter.out.persistence;

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
 * Tabla {@code per_empleado_area}: área asignada a un empleado.
 *
 * <p>El área es del módulo Áreas; solo se guarda su id y nombre, sin FK hacia {@code are_area}.
 */
@Entity
@Table(name = "per_empleado_area")
public class EmpleadoAreaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private EmpleadoEntity empleado;

    @Column(name = "area_id", nullable = false)
    private Integer areaId;

    @Column(name = "nombre_area", nullable = false, length = 150)
    private String nombreArea;

    protected EmpleadoAreaEntity() {
        // Requerido por JPA.
    }

    public Long getId() {
        return id;
    }

    public EmpleadoEntity getEmpleado() {
        return empleado;
    }

    void setEmpleado(EmpleadoEntity empleado) {
        this.empleado = empleado;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getNombreArea() {
        return nombreArea;
    }

    public void setNombreArea(String nombreArea) {
        this.nombreArea = nombreArea;
    }
}
