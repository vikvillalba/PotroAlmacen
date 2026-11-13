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
public final class Material {

    public static final int NOMBRE_MAXIMO = 500;
    public static final int CONTENIDO_PRESENTACION_MAXIMO = 120;
    public static final int USO_PRECAUCIONES_MAXIMO = 2000;

    private final Integer id;
    private final CodigoMaterial codigo;
    private final Campus campus;
    private String nombre;
    private Categoria categoria;
    private UnidadMedida unidadMedida;
    private Presentacion presentacion;
    private String contenidoPresentacion;
    private BigDecimal precioUnitario;
    private int cantidad;
    private int stockMinimo;
    private Integer stockMaximo;
    private String usoPrecauciones;

    private Material(
            Integer id,
            CodigoMaterial codigo,
            String nombre,
            Categoria categoria,
            UnidadMedida unidadMedida,
            Presentacion presentacion,
            String contenidoPresentacion,
            Campus campus,
            BigDecimal precioUnitario,
            int cantidad,
            int stockMinimo,
            Integer stockMaximo,
            String usoPrecauciones) {
        this.id = id;
        this.codigo = Objects.requireNonNull(codigo, "El código es obligatorio");
        this.nombre = requerirTexto(nombre, "nombre", NOMBRE_MAXIMO);
        this.categoria = requerir(categoria, "categoría");
        this.unidadMedida = requerir(unidadMedida, "unidad de medida");
        this.presentacion = requerir(presentacion, "presentación");
        this.contenidoPresentacion =
                opcional(contenidoPresentacion, "contenido de la presentación", CONTENIDO_PRESENTACION_MAXIMO);
        this.campus = requerir(campus, "campus");
        this.precioUnitario = validarPrecio(precioUnitario);
        this.cantidad = validarNoNegativo(cantidad, "La cantidad");
        this.stockMinimo = validarNoNegativo(stockMinimo, "El stock mínimo");
        this.stockMaximo = validarStockMaximo(stockMaximo, this.stockMinimo);
        this.usoPrecauciones = opcional(usoPrecauciones, "uso y precauciones", USO_PRECAUCIONES_MAXIMO);
    }

    public static Material crear(
            CodigoMaterial codigo,
            String nombre,
            Categoria categoria,
            UnidadMedida unidadMedida,
            Presentacion presentacion,
            String contenidoPresentacion,
            Campus campus,
            BigDecimal precioUnitario,
            int stockMinimo,
            Integer stockMaximo,
            String usoPrecauciones) {
        return crear(
                codigo,
                nombre,
                categoria,
                unidadMedida,
                presentacion,
                contenidoPresentacion,
                campus,
                precioUnitario,
                0,
                stockMinimo,
                stockMaximo,
                usoPrecauciones);
    }

    public static Material crear(
            CodigoMaterial codigo,
            String nombre,
            Categoria categoria,
            UnidadMedida unidadMedida,
            Presentacion presentacion,
            String contenidoPresentacion,
            Campus campus,
            BigDecimal precioUnitario,
            int cantidadInicial,
            int stockMinimo,
            Integer stockMaximo,
            String usoPrecauciones) {
        // Valida que la cantidad inicial sea menor o igual que el stock máximo
        validarNoNegativo(cantidadInicial, "La existencia inicial");
        if (stockMaximo != null && cantidadInicial > stockMaximo) {
            throw new MaterialInvalidoException("La existencia inicial no puede rebasar el stock máximo");
        }
        return new Material(
                null,
                codigo,
                nombre,
                categoria,
                unidadMedida,
                presentacion,
                contenidoPresentacion,
                campus,
                precioUnitario,
                cantidadInicial,
                stockMinimo,
                stockMaximo,
                usoPrecauciones);
    }

    /** Reconstruye un material ya persistido. Lo usa el adaptador de persistencia. */
    public static Material reconstruir(
            Integer id,
            CodigoMaterial codigo,
            String nombre,
            Categoria categoria,
            UnidadMedida unidadMedida,
            Presentacion presentacion,
            String contenidoPresentacion,
            Campus campus,
            BigDecimal precioUnitario,
            int cantidad,
            int stockMinimo,
            Integer stockMaximo,
            String usoPrecauciones) {
        Objects.requireNonNull(id, "El id es obligatorio al reconstruir un material");
        return new Material(
                id,
                codigo,
                nombre,
                categoria,
                unidadMedida,
                presentacion,
                contenidoPresentacion,
                campus,
                precioUnitario,
                cantidad,
                stockMinimo,
                stockMaximo,
                usoPrecauciones);
    }

    public Material conId(Integer nuevoId) {
        return reconstruir(
                nuevoId,
                codigo,
                nombre,
                categoria,
                unidadMedida,
                presentacion,
                contenidoPresentacion,
                campus,
                precioUnitario,
                cantidad,
                stockMinimo,
                stockMaximo,
                usoPrecauciones);
    }

    public void actualizarDatos(
            String nombre,
            Categoria categoria,
            UnidadMedida unidadMedida,
            Presentacion presentacion,
            String contenidoPresentacion) {
        // Se valida todo primero para no dejar el objeto a medias si algo falla.
        String nuevoNombre = requerirTexto(nombre, "nombre", NOMBRE_MAXIMO);
        Categoria nuevaCategoria = requerir(categoria, "categoría");
        UnidadMedida nuevaUnidad = requerir(unidadMedida, "unidad de medida");
        Presentacion nuevaPresentacion = requerir(presentacion, "presentación");
        String nuevoContenido =
                opcional(contenidoPresentacion, "contenido de la presentación", CONTENIDO_PRESENTACION_MAXIMO);
        this.nombre = nuevoNombre;
        this.categoria = nuevaCategoria;
        this.unidadMedida = nuevaUnidad;
        this.presentacion = nuevaPresentacion;
        this.contenidoPresentacion = nuevoContenido;
    }

    /** Pasar null o texto en blanco lo deja vacío. */
    public void actualizarUsoPrecauciones(String nuevoTexto) {
        this.usoPrecauciones = opcional(nuevoTexto, "uso y precauciones", USO_PRECAUCIONES_MAXIMO);
    }

    /** Precio unitario sin IVA. En la recepción de material, el IVA se calcula aparte. */
    public void actualizarPrecio(BigDecimal nuevoPrecio) {
        this.precioUnitario = validarPrecio(nuevoPrecio);
    }

    // existencia

    public void registrarEntrada(int cantidadRecibida) {
        if (cantidadRecibida <= 0) {
            throw new MaterialInvalidoException("La cantidad de entrada debe ser mayor a cero");
        }
        int nuevaCantidad = Math.addExact(this.cantidad, cantidadRecibida);
        if (stockMaximo != null && nuevaCantidad > stockMaximo) {
            throw new MaterialInvalidoException("La existencia no puede rebasar el stock máximo");
        }
        this.cantidad = nuevaCantidad;
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
        int validado = validarNoNegativo(nuevoStockMinimo, "El stock mínimo");
        validarStockMaximo(this.stockMaximo, validado);
        this.stockMinimo = validado;
    }

    /** Pasar null quita el stock máximo. */
    public void cambiarStockMaximo(Integer nuevoStockMaximo) {
        this.stockMaximo = validarStockMaximo(nuevoStockMaximo, this.stockMinimo);
    }

    public boolean estaBajoMinimo() {
        return cantidad < stockMinimo;
    }

    // validaciones

    private static <T> T requerir(T valor, String campo) {
        if (valor == null) {
            throw new MaterialInvalidoException("El campo " + campo + " es obligatorio");
        }
        return valor;
    }

    private static String requerirTexto(String valor, String campo, int max) {
        if (valor == null || valor.isBlank()) {
            throw new MaterialInvalidoException("El campo " + campo + " es obligatorio");
        }
        return limitar(valor.trim(), campo, max);
    }

    private static String opcional(String valor, String campo, int max) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return limitar(valor.trim(), campo, max);
    }

    private static String limitar(String valor, String campo, int max) {
        if (valor.length() > max) {
            throw new MaterialInvalidoException("El campo " + campo + " no puede exceder " + max + " caracteres");
        }
        return valor;
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

    private static Integer validarStockMaximo(Integer maximo, int minimo) {
        if (maximo == null) {
            return null;
        }
        if (maximo < 0) {
            throw new MaterialInvalidoException("El stock máximo no puede ser negativo");
        }
        if (maximo <= minimo) {
            throw new MaterialInvalidoException("El stock mínimo debe ser menor que el stock máximo");
        }
        return maximo;
    }

    public Integer getId() {
        return id;
    }

    public CodigoMaterial getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public Presentacion getPresentacion() {
        return presentacion;
    }

    public String getContenidoPresentacion() {
        return contenidoPresentacion;
    }

    /** Departamento al que pertenece el material, según su categoría. */
    public Departamento getDepartamento() {
        return categoria.getDepartamento();
    }

    public Campus getCampus() {
        return campus;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public Integer getStockMaximo() {
        return stockMaximo;
    }

    public String getUsoPrecauciones() {
        return usoPrecauciones;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Material otro && codigo.equals(otro.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }
}
