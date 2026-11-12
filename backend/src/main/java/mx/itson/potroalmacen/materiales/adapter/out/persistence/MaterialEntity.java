package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;

@Entity
@Table(name = "mat_material")
public class MaterialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 500)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 120)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false, length = 60)
    private UnidadMedida unidadMedida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private Presentacion presentacion;

    @Column(name = "contenido_presentacion", length = 120)
    private String contenidoPresentacion;

    @Enumerated(EnumType.STRING)
    @Column(length = 80, nullable = false)
    private Campus campus;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "stock_minimo", nullable = false)
    private int stockMinimo;

    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    @Column(name = "uso_precauciones", columnDefinition = "TEXT")
    private String usoPrecauciones;

    public MaterialEntity() {}

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Presentacion getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(Presentacion presentacion) {
        this.presentacion = presentacion;
    }

    public String getContenidoPresentacion() {
        return contenidoPresentacion;
    }

    public void setContenidoPresentacion(String contenidoPresentacion) {
        this.contenidoPresentacion = contenidoPresentacion;
    }

    public Campus getCampus() {
        return campus;
    }

    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(Integer stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public String getUsoPrecauciones() {
        return usoPrecauciones;
    }

    public void setUsoPrecauciones(String usoPrecauciones) {
        this.usoPrecauciones = usoPrecauciones;
    }
}
