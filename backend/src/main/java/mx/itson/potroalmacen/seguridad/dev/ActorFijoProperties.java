package mx.itson.potroalmacen.seguridad.dev;

import mx.itson.potroalmacen.seguridad.Actor;
import mx.itson.potroalmacen.seguridad.Rol;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Datos del actor fijo de desarrollo, leídos de {@code potro.seguridad.actor-fijo} en application-dev.yml.
 *
 * @param usuarioId id del usuario
 * @param rol rol del usuario
 * @param almacenId id del almacén asignado
 */
@ConfigurationProperties(prefix = "potro.seguridad.actor-fijo")
record ActorFijoProperties(Long usuarioId, Rol rol, Long almacenId) {

    Actor toActor() {
        return new Actor(usuarioId, rol, almacenId);
    }
}
