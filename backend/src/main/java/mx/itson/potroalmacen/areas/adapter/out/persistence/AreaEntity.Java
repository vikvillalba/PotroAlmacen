package mx.itson.potroalmacen.areas.adapter.out.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import mx.itson.potroalmacen.areas.domain.TipoArea;

/**
 * Tabla {@code are_area}: un área (edificio, laboratorio, etc.) con sus espacios y los materiales permitidos.
 *
 * <p>Los espacios y los materiales permitidos son del mismo módulo, así que se relacionan directamente con
 * {@link EspacioEntity} y {@link AreaMaterialEntity}. Los materiales son del módulo Materiales y solo se guarda su
 * código y nombre, sin FK ni relación JPA hacia {@code mat_material}.
 */
@Entity
@Table(name = "are_area")
public class AreaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_area", nullable = false, unique = true, length = 150)
    private String nombreArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_area", nullable = false, length = 40)
    private TipoArea tipoArea;

    @Column(name = "metros_cuadrados", nullable = false, precision = 10, scale = 2)
    private BigDecimal metrosCuadrados;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EspacioEntity> espacios = new ArrayList<>();

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AreaMaterialEntity> materiales = new ArrayList<>();

    @Version
    private Long version;

    protected AreaEntity() {
        // Requerido por JPA.
    }

    /** Agrega un espacio y mantiene los dos lados de la relación sincronizados. */
    public void agregarEspacio(EspacioEntity espacio) {
        espacio.setArea(this);
        espacios.add(espacio);
    }

    /** Agrega un material permitido y mantiene los dos lados de la relación sincronizados. */
    public void agregarMaterial(AreaMaterialEntity material) {
        material.setArea(this);
        materiales.add(material);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreArea() { return nombreArea; }
    public void setNombreArea(String nombreArea) { this.nombreArea = nombreArea; }

    public TipoArea getTipoArea() { return tipoArea; }
    public void setTipoArea(TipoArea tipoArea) { this.tipoArea = tipoArea; }

    public BigDecimal getMetrosCuadrados() { return metrosCuadrados; }
    public void setMetrosCuadrados(BigDecimal metrosCuadrados) { this.metrosCuadrados = metrosCuadrados; }

    public List<EspacioEntity> getEspacios() { return espacios; }

    public List<AreaMaterialEntity> getMateriales() { return materiales; }

    public Long getVersion() { return version; }
}