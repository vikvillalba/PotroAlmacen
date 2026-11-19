package mx.itson.potroalmacen.materiales.application.port.in;

/**
 * Puerto de entrada para registrar la salida de un material del inventario.
 *
 * <p>Materiales es el único dueño del inventario: otros módulos (por ejemplo, Salidas al entregar una boleta) lo usan
 * para descontar existencias.
 */
public interface RegistrarSalidaMaterialUseCase {

    /**
     * Verifica la existencia del material y descuenta la cantidad indicada.
     *
     * @param codigoMaterial código del material.
     * @param cantidad unidades que salen; mayor a cero.
     * @return el código, el nombre y la cantidad que salió.
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.MaterialNoEncontradoException si no existe un
     *     material con ese código.
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.ExistenciaInsuficienteException si la cantidad
     *     supera la existencia del material.
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException si la cantidad no es
     *     mayor a cero.
     */
    SalidaRegistrada registrarSalida(String codigoMaterial, int cantidad);
}
