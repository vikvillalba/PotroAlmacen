package mx.itson.potroalmacen.compartido.auditoria;

import java.time.Instant;

/**
 * Evento de dominio que debe quedar registrado en la bitácora de auditoría.
 *
 * <p>Es Java puro. Los métodos siguen el estilo de los records para que un evento declarado como {@code record}
 * pueda implementar la interfaz directamente con sus componentes.
 */
public interface EventoAuditable {

    /**
     * Usuario que realizó la acción.
     *
     * @return id del usuario
     */
    Long usuarioId();

    /**
     * Campus donde ocurrió la acción.
     *
     * @return nombre del campus, por ejemplo {@code OBREGON_NAINARI}
     */
    String campus();

    /**
     * Acción realizada.
     *
     * @return la acción auditada
     */
    AccionAuditoria accion();

    /**
     * Módulo que originó el evento.
     *
     * @return nombre del módulo, por ejemplo {@code materiales}
     */
    String modulo();

    /**
     * Entidad afectada.
     *
     * @return nombre de la entidad, por ejemplo {@code Material}
     */
    String entidad();

    /**
     * Referencia legible del registro afectado.
     *
     * @return identificador de negocio, por ejemplo el folio
     */
    String referencia();

    /**
     * Momento en que ocurrió la acción.
     *
     * @return instante del evento
     */
    Instant ocurridoEn();
}
