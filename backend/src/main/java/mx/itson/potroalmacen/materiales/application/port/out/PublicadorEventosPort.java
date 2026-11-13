package mx.itson.potroalmacen.materiales.application.port.out;

/**
 * Sujeto del patrón Observer: notifica un evento de dominio a todos los suscriptores interesados.
 *
 * <p>Los casos de uso publican eventos sin saber quién los escucha ni cuántos suscriptores hay (puede no haber
 * ninguno). Los suscriptores se registran en otros módulos con {@code @ApplicationModuleListener}.
 */
public interface PublicadorEventosPort {

    /**
     * Notifica el evento a los suscriptores.
     *
     * @param evento evento de dominio, por ejemplo {@link mx.itson.potroalmacen.materiales.eventos.MaterialRegistrado}.
     */
    void publicar(Object evento);
}
