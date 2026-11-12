package mx.itson.potroalmacen.materiales.application.port.in;

import java.math.BigDecimal;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException;

/**
 * Comando que encapsula los datos requeridos para registrar un material
 * con su existencia inicial y parámetros de inventario para un campus específico.
 */
public record RegistrarMaterialCommand(
        String codigo,
        String nombre,
        String categoria,
        String unidadMedida,
        String presentacion,
        String campus,
        BigDecimal precioUnitario,
        int existenciaInicial,
        int stockMinimo,
        Integer stockMaximo,
        String usoPrecauciones) {
    public RegistrarMaterialCommand {
        if (existenciaInicial < 0) {
            throw new MaterialInvalidoException("La existencia inicial no puede ser negativa");
        }
    }
}
