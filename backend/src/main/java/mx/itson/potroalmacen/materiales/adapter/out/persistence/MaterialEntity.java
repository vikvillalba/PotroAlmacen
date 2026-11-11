package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "mat_ficha_tecnica")
public class MaterialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 120, nullable = false)
    private String categoria;

    @Column(name = "unidad_medida", length = 60, nullable = false)
    private String unidadMedida;

    @Column(length = 80, nullable = false)
    private String presentacion;

    @Column(length = 120, nullable = false)
    private String departamento;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    // Campos aplanados o mapeados desde FichaTecnica del dominio
    @Column(name = "caracteristicas_tecnicas", columnDefinition = "TEXT")
    private String caracteristicasTecnicas;

    @Column(name = "marca_requerida", length = 100)
    private String marcaRequerida;

    @Column(length = 255)
    private String normas;

    @Column(name = "requiere_hoja_seguridad", nullable = false)
    private boolean requiereHojaSeguridad;

    @Column(name = "uso_y_precauciones", columnDefinition = "TEXT")
    private String usoYPrecauciones;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public MaterialEntity() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public String getCaracteristicasTecnicas() { return caracteristicasTecnicas; }
    public void setCaracteristicasTecnicas(String caracteristicasTecnicas) { this.caracteristicasTecnicas = caracteristicasTecnicas; }
    public String getMarcaRequerida() { return marcaRequerida; }
    public void setMarcaRequerida(String marcaRequerida) { this.marcaRequerida = marcaRequerida; }
    public String getNormas() { return normas; }
    public void setNormas(String normas) { this.normas = normas; }
    public boolean isRequiereHojaSeguridad() { return requiereHojaSeguridad; }
    public void setRequiereHojaSeguridad(boolean requiereHojaSeguridad) { this.requiereHojaSeguridad = requiereHojaSeguridad; }
    public String getUsoYPrecauciones() { return usoYPrecauciones; }
    public void setUsoYPrecauciones(String usoYPrecauciones) { this.usoYPrecauciones = usoYPrecauciones; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}