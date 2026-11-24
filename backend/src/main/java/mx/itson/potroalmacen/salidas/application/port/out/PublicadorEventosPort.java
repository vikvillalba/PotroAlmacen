package mx.itson.potroalmacen.salidas.application.port.out;

/**
 * Sujeto del patrón Observer: notifica un evento de dominio de Salidas a todos los suscriptores interesados.
 *
 * <p>Los casos de uso publican eventos sin saber quién los escucha ni cuántos suscriptores hay (puede no haber
 * ninguno). Por ejemplo, Auditoría escucha los eventos que implementan
 * {@link mx.itson.potroalmacen.compartido.auditoria.EventoAuditable}.
 */
public interface PublicadorEventosPort {

    /**
     * Notifica el evento a los suscriptores.
     *
     * @param evento evento de dominio, por ejemplo {@link mx.itson.potroalmacen.salidas.eventos.BoletaGenerada}.
     */
    void publicar(Object evento);
}
