package mx.itson.potroalmacen.salidas.application.port.in;

import mx.itson.potroalmacen.salidas.domain.Boleta;

/**
 * Puerto de entrada para el caso de uso de solicitud de entrega de un listado de materiales.
 * Define la operación para generar la boleta de entrega a partir de la solicitud.
 */
public interface SolicitarEntregaUseCase {

    /**
     * Registra la entrega de los materiales solicitados y genera la boleta correspondiente.
     *
     * <p>La existencia de cada material la verifica el módulo Materiales al registrar la salida.
     *
     * @param comando solicitante, entregador y listado de materiales con la cantidad a entregar.
     * @return la boleta creada y persistida.
     * @throws mx.itson.potroalmacen.salidas.domain.exceptions.BoletaInvalidaException si la solicitud
     *     no cumple las reglas de la boleta (solicitante o entregador faltante, misma persona, o sin
     *     materiales).
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.ExistenciaInsuficienteException si la
     *     cantidad a entregar de algún material supera su existencia (la lanza el módulo Materiales).
     */
    Boleta solicitar(SolicitudEntregaCommand comando);
}
