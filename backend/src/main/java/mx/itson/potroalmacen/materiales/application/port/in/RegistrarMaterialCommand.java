package mx.itson.potroalmacen.materiales.application.port.in;

import java.math.BigDecimal;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;

/**
 * Comando que encapsula los datos requeridos para registrar un material
 * con su existencia inicial y parámetros de inventario para un campus específico.
 */
public record RegistrarMaterialCommand(
        String codigo,
        String nombre,
        Categoria categoria,
        UnidadMedida unidadMedida,
        Presentacion presentacion,
        String contenidoPresentacion,
        Campus campus,
        BigDecimal precioUnitario,
        int existenciaInicial,
        int stockMinimo,
        Integer stockMaximo,
        String usoPrecauciones) {}
