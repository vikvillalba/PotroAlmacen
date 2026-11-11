/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.usuarios.domain;

import java.math.BigDecimal;
import java.util.Objects;
import mx.itson.potroalmacen.usuarios.domain.exceptions.MaterialInvalidoException;

/**
 *una ficha técnica / material del catálogo.
 * Sin dependencies de Spring ni JPA.
 * @author erika
 */
public class Material {
    
    public static final int NOMBRE_MAXIMO = 500;
    
    private final Long id;
    private final CodigoMaterial codigo;
    private String nombre;
    private Long categoriaId;
    private Long unidadMedidaId;
    private Long presentacionId;
    private Long departamentoId;
    private BigDecimal precioUnitario;
    private FichaTecnica ficha;
    
    private Material(Long id, CodigoMaterial codigo, String nombre, Long categoriaId,
                     Long unidadMedidaId, Long presentacionId, Long departamentoId,
                     BigDecimal precioUnitario, FichaTecnica ficha) {
        this.id = id;
        this.codigo = Objects.requireNonNull(codigo, "El código es obligatorio");
        this.nombre = requerirNombre(nombre);
        this.categoriaId = requerirId(categoriaId, "categoría");
        this.unidadMedidaId = requerirId(unidadMedidaId, "unidad de medida");
        this.presentacionId = requerirId(presentacionId, "presentación");
        this.departamentoId = requerirId(departamentoId, "departamento");
        this.precioUnitario = validarPrecio(precioUnitario);
        this.ficha = ficha == null ? FichaTecnica.vacia() : ficha;
    }
    
   
    public static Material crear(CodigoMaterial codigo, String nombre, Long categoriaId, Long unidadMedidaId, Long presentacionId, Long departamentoId, BigDecimal precioUnitario, FichaTecnica ficha) {
        return new Material(null, codigo, nombre, categoriaId, unidadMedidaId, presentacionId, departamentoId, precioUnitario, ficha);
    }
    
    public static Material reconstruir(Long id, CodigoMaterial codigo, String nombre, Long categoriaId, Long unidadMedidaId, Long presentacionId, Long departamentoId,BigDecimal precioUnitario, FichaTecnica ficha) {
        Objects.requireNonNull(id, "El id es obligatorio al reconstruir un material");
        return new Material(id, codigo, nombre, categoriaId, unidadMedidaId, presentacionId, departamentoId, precioUnitario, ficha);
    }
    
     public Material conId(Long nuevoId) {
        return reconstruir(nuevoId, codigo, nombre, categoriaId, unidadMedidaId, presentacionId, departamentoId, precioUnitario, ficha);
    }
     
    public void actualizarDatos(String nombre, Long categoriaId, Long unidadMedidaId,Long presentacionId, Long departamentoId) {
        String nuevoNombre = requerirNombre(nombre);
        Long nuevaCategoria = requerirId(categoriaId, "categoría");
        Long nuevaUnidad = requerirId(unidadMedidaId, "unidad de medida");
        Long nuevaPresentacion = requerirId(presentacionId, "presentación");
        Long nuevoDepartamento = requerirId(departamentoId, "departamento");
        this.nombre = nuevoNombre;
        this.categoriaId = nuevaCategoria;
        this.unidadMedidaId = nuevaUnidad;
        this.presentacionId = nuevaPresentacion;
        this.departamentoId = nuevoDepartamento;
    }
    
    public void actualizarFicha(FichaTecnica nuevaFicha) {
        this.ficha = nuevaFicha == null ? FichaTecnica.vacia() : nuevaFicha;
    }
    
    public void actualizarPrecio(BigDecimal nuevoPrecio) {
        this.precioUnitario = validarPrecio(nuevoPrecio);
    }
    
    private static String requerirNombre(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new MaterialInvalidoException("El campo nombre es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > NOMBRE_MAXIMO) {
            throw new MaterialInvalidoException(
                    "El nombre no puede exceder " + NOMBRE_MAXIMO + " caracteres");
        }
        return limpio;
    }
    
    private static Long requerirId(Long valor, String campo) {
        if (valor == null) {
            throw new MaterialInvalidoException("El campo " + campo + " es obligatorio");
        }
        return valor;
    }
    
    private static BigDecimal validarPrecio(BigDecimal precio) {
        if (precio != null && precio.signum() < 0) {
            throw new MaterialInvalidoException("El precio unitario no puede ser negativo");
        }
        return precio;
    }
    
    public Long getId() { return id; }
    public CodigoMaterial getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public Long getCategoriaId() { return categoriaId; }
    public Long getUnidadMedidaId() { return unidadMedidaId; }
    public Long getPresentacionId() { return presentacionId; }
    public Long getDepartamentoId() { return departamentoId; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public FichaTecnica getFicha() { return ficha; }
    
    @Override
    public boolean equals(Object o) {
        return o instanceof Material otro && codigo.equals(otro.codigo);
    }
    
    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
}
