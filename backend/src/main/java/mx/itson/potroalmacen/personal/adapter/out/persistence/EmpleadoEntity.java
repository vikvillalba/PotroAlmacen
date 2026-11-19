package mx.itson.potroalmacen.personal.adapter.out.persistence;

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
import java.util.ArrayList;
import java.util.List;
import mx.itson.potroalmacen.personal.domain.TipoEmpleado;

/**
 * Tabla {@code per_empleado}: empleado interno (ITSON) o externo que puede solicitar materiales.
 *
 * <p>Las áreas asignadas son del módulo Áreas, así que se guardan en {@link EmpleadoAreaEntity} solo con el id y el
 * nombre del área, sin FK ni relación JPA hacia {@code are_area}.
 */
@Entity
@Table(name = "per_empleado")
public class EmpleadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 100)
    private String apellidoMaterno;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_empleado", nullable = false, length = 20)
    private TipoEmpleado tipoEmpleado;

    @Column(nullable = false, length = 120)
    private String puesto;

    @Column(name = "jefe_directo", length = 200)
    private String jefeDirecto;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "en_prestamo", nullable = false)
    private boolean enPrestamo = false;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmpleadoAreaEntity> areasAsignadas = new ArrayList<>();

    @Version
    private Long version;

    protected EmpleadoEntity() {
        // Requerido por JPA.
    }

    /** Agrega un área asignada y mantiene los dos lados de la relación sincronizados. */
    public void agregarArea(EmpleadoAreaEntity area) {
        area.setEmpleado(this);
        areasAsignadas.add(area);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public TipoEmpleado getTipoEmpleado() {
        return tipoEmpleado;
    }

    public void setTipoEmpleado(TipoEmpleado tipoEmpleado) {
        this.tipoEmpleado = tipoEmpleado;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getJefeDirecto() {
        return jefeDirecto;
    }

    public void setJefeDirecto(String jefeDirecto) {
        this.jefeDirecto = jefeDirecto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isEnPrestamo() {
        return enPrestamo;
    }

    public void setEnPrestamo(boolean enPrestamo) {
        this.enPrestamo = enPrestamo;
    }

    public List<EmpleadoAreaEntity> getAreasAsignadas() {
        return areasAsignadas;
    }

    public Long getVersion() {
        return version;
    }
}
