package mx.itson.potroalmacen.areas.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mx.itson.potroalmacen.areas.domain.FrecuenciaLimpieza;

/** Tabla {@code are_espacio}: un espacio (aula, oficina, baño...) que pertenece a un área. */
@Entity
@Table(name = "are_espacio")
public class EspacioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private AreaEntity area;

    @Column(name = "nombre_espacio", nullable = false, length = 150)
    private String nombreEspacio;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "frecuencia_limpieza", nullable = false, length = 30)
    private FrecuenciaLimpieza frecuenciaLimpieza;

    protected EspacioEntity() {
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

    public String getNombreEspacio() {
        return nombreEspacio;
    }

    public void setNombreEspacio(String nombreEspacio) {
        this.nombreEspacio = nombreEspacio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public FrecuenciaLimpieza getFrecuenciaLimpieza() {
        return frecuenciaLimpieza;
    }

    public void setFrecuenciaLimpieza(FrecuenciaLimpieza frecuenciaLimpieza) {
        this.frecuenciaLimpieza = frecuenciaLimpieza;
    }
}
