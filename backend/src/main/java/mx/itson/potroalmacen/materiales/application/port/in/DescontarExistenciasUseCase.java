package mx.itson.potroalmacen.materiales.application.port.in;

import java.util.List;

/**
 * Puerto de entrada para consultar las existencias de un listado de materiales y descontarlas del inventario.
 *
 * <p>Materiales es el único dueño del inventario: otros módulos (por ejemplo, Salidas al entregar una boleta) lo usan
 * en lugar de tocar sus tablas. A diferencia de {@link RegistrarSalidaMaterialUseCase}, que descuenta un material a la
 * vez, este caso de uso trabaja con el listado completo.
 *
 * <p>Reglas que respeta quien lo implemente:
 * <ul>
 *   <li>Una existencia nunca queda negativa.
 *   <li>El descuento es todo o nada: si falla un material, no se descuenta ninguno.
 *   <li>Si una solicitud repite un código, sus cantidades se suman antes de verificar la existencia.
 * </ul>
 */
public interface DescontarExistenciasUseCase {

    /**
     * Consulta la existencia actual de cada material, sin modificar el inventario.
     *
     * @param codigosMaterial códigos de los materiales; no puede estar vacío.
     * @return la existencia de cada material, en el mismo orden que los códigos recibidos.
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.MaterialNoEncontradoException si no existe un
     *     material con alguno de los códigos.
     */
    List<ExistenciaMaterial> consultarExistencias(List<String> codigosMaterial);

    /**
     * Verifica la existencia de cada material de la solicitud y la descuenta, todo en una sola transacción.
     *
     * <p>Si algún material no existe, la cantidad no es válida o supera su existencia, se lanza la excepción y el
     * inventario queda como estaba.
     *
     * @param solicitud materiales y cantidades que salen; no puede estar vacía.
     * @return el código, el nombre y la cantidad que salió de cada material, en el orden de la solicitud.
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.MaterialNoEncontradoException si no existe un
     *     material con alguno de los códigos.
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.ExistenciaInsuficienteException si la cantidad
     *     de algún material supera su existencia.
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException si alguna cantidad no
     *     es mayor a cero.
     */
    List<SalidaRegistrada> descontarExistencias(SolicitudSalidaMateriales solicitud);
}