/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.materiales.domain;

import java.math.BigDecimal;
import java.util.Objects;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException;

/**
 *
 * @author erika
 */
public class Material {
    
    public static final int NOMBRE_MAXIMO = 500;
    public static final int CATEGORIA_MAXIMA = 120;
    public static final int UNIDAD_MEDIDA_MAXIMA = 60;
    public static final int PRESENTACION_MAXIMA = 80;
    public static final int DEPARTAMENTO_MAXIMO = 120;
 
    private final Integer id;
    private final CodigoMaterial codigo;
    private String nombre;
    private String categoria;
    private String unidadMedida;
    private String presentacion;
    private String departamento;
    private BigDecimal precioUnitario;
    private int cantidad;
    private int stockMinimo;
    private FichaTecnica ficha;
 
    private Material(Integer id, CodigoMaterial codigo, String nombre, String categoria,
                     String unidadMedida, String presentacion, String departamento,
                     BigDecimal precioUnitario, int cantidad, int stockMinimo,
                     FichaTecnica ficha) {
        this.id = id;
        this.codigo = Objects.requireNonNull(codigo, "El código es obligatorio");
        this.nombre = requerirTexto(nombre, "nombre", NOMBRE_MAXIMO);
        this.categoria = requerirTexto(categoria, "categoría", CATEGORIA_MAXIMA);
        this.unidadMedida = requerirTexto(unidadMedida, "unidad de medida", UNIDAD_MEDIDA_MAXIMA);
        this.presentacion = requerirTexto(presentacion, "presentacion", PRESENTACION_MAXIMA);
        this.departamento = requerirTexto(departamento, "departamento", DEPARTAMENTO_MAXIMO);
        this.precioUnitario = validarPrecio(precioUnitario);
        this.cantidad = validarNoNegativo(cantidad, " la cantidad");
        this.stockMinimo = validarNoNegativo(stockMinimo, "el stock minimo");
        this.ficha = ficha == null ? FichaTecnica.vacia() : ficha;
    }
 
    public static Material crear(CodigoMaterial codigo, String nombre, String categoria,
                                 String unidadMedida, String presentacion, String departamento,
                                 BigDecimal precioUnitario, int stockMinimo, FichaTecnica ficha) {
        return new Material(null, codigo, nombre, categoria, unidadMedida, presentacion,
                departamento, precioUnitario, 0, stockMinimo, ficha);
    }
 
    public static Material reconstruir(Integer id, CodigoMaterial codigo, String nombre, String categoria,
                                       String unidadMedida, String presentacion, String departamento,
                                       BigDecimal precioUnitario, int cantidad, int stockMinimo,
                                       FichaTecnica ficha) {
        Objects.requireNonNull(id, "El id es obligatorio al reconstruir un material");
        return new Material(id, codigo, nombre, categoria, unidadMedida, presentacion,
                departamento, precioUnitario, cantidad, stockMinimo, ficha);
    }
 
    public Material conId(Integer nuevoId) {
        return reconstruir(nuevoId, codigo, nombre, categoria, unidadMedida, presentacion,
                departamento, precioUnitario, cantidad, stockMinimo, ficha);
    }
 

    public void actualizarDatos(String nombre, String categoria, String unidadMedida,
                                String presentacion, String departamento) {
        // valida todo primero para no dejar el objeto a medias si algo falla.
        String nuevoNombre = requerirTexto(nombre, "nombre", NOMBRE_MAXIMO);
        String nuevaCategoria = requerirTexto(categoria, "categoría", CATEGORIA_MAXIMA);
        String nuevaUnidad = requerirTexto(unidadMedida, "unidad de medida", UNIDAD_MEDIDA_MAXIMA);
        String nuevaPresentacion = requerirTexto(presentacion, "presentación", PRESENTACION_MAXIMA);
        String nuevoDepartamento = requerirTexto(departamento, "departamento", DEPARTAMENTO_MAXIMO);
        this.nombre = nuevoNombre;
        this.categoria = nuevaCategoria;
        this.unidadMedida = nuevaUnidad;
        this.presentacion = nuevaPresentacion;
        this.departamento = nuevoDepartamento;
    }
 
    public void actualizarFicha(FichaTecnica nuevaFicha) {
        this.ficha = nuevaFicha == null ? FichaTecnica.vacia() : nuevaFicha;
    }
 
    public void actualizarPrecio(BigDecimal nuevoPrecio) {
        this.precioUnitario = validarPrecio(nuevoPrecio);
    }
 
 
    public void registrarEntrada(int cantidadRecibida) {
        if (cantidadRecibida <= 0) {
            throw new MaterialInvalidoException("La cantidad de entrada debe ser mayor a cero");
        }
        this.cantidad = Math.addExact(this.cantidad, cantidadRecibida);
    }
 
    public void registrarSalida(int cantidadSolicitada) {
        if (cantidadSolicitada <= 0) {
            throw new MaterialInvalidoException("La cantidad de salida debe ser mayor a cero");
        }
        if (cantidadSolicitada > cantidad) {
            throw new MaterialInvalidoException("Cantidad solicitada excede las existencias disponibles");
        }
        this.cantidad -= cantidadSolicitada;
    }
 
    public void cambiarStockMinimo(int nuevoStockMinimo) {
        this.stockMinimo = validarNoNegativo(nuevoStockMinimo, "El stock mínimo");
    }
 
    public boolean estaBajoMinimo() {
        return cantidad < stockMinimo;
    }
 
 
    private static String requerirTexto(String valor, String campo, int max) {
        if (valor == null || valor.isBlank()) {
            throw new MaterialInvalidoException("El campo " + campo + " es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > max) {
            throw new MaterialInvalidoException(
                    "El campo " + campo + " no puede exceder " + max + " caracteres");
        }
        return limpio;
    }
 
    private static BigDecimal validarPrecio(BigDecimal precio) {
        if (precio != null && precio.signum() < 0) {
            throw new MaterialInvalidoException("El precio unitario no puede ser negativo");
        }
        return precio;
    }
 
    private static int validarNoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new MaterialInvalidoException(campo + " no puede ser negativo");
        }
        return valor;
    }
 
    public Integer getId() { return id; }
    public CodigoMaterial getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getUnidadMedida() { return unidadMedida; }
    public String getPresentacion() { return presentacion; }
    public String getDepartamento() { return departamento; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public int getCantidad() { return cantidad; }
    public int getStockMinimo() { return stockMinimo; }
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
