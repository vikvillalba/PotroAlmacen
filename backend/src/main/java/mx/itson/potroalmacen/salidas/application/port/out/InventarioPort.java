package mx.itson.potroalmacen.salidas.application.port.out;

import mx.itson.potroalmacen.salidas.domain.MaterialRef;

/**
 * Puerto de salida hacia el inventario.
 *
 * <p>El módulo Materiales es el dueño del inventario: el adaptador que lo implemente le pide registrar la salida, y
 * es Materiales quien verifica la existencia y la descuenta.
 */
public interface InventarioPort {

    /**
     * Registra la salida de un material del inventario.
     *
     * @param codigoMaterial código del material.
     * @param cantidad unidades que salen; mayor a cero.
     * @return la referencia del material entregado (código, nombre y cantidad).
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.ExistenciaInsuficienteException si la cantidad
     *     supera la existencia del material.
     */
    MaterialRef registrarSalida(String codigoMaterial, int cantidad);
}
