package mx.itson.potroalmacen.seguridad;

import java.util.Objects;

/**
 * Usuario que ejecuta una operación, ya traducido desde la autenticación.
 *
 * <p>Viaja en los comandos de los casos de uso para que las reglas de autorización de negocio (por ejemplo, "solo
 * puedes registrar salidas de tu almacén") vivan en el dominio. No depende de Spring.
 *
 * @param usuarioId id del usuario
 * @param rol rol del usuario
 * @param almacenId id del almacén (campus) asignado al usuario
 */
public record Actor(Long usuarioId, Rol rol, Long almacenId) {

    public Actor {
        Objects.requireNonNull(usuarioId, "El usuarioId del actor es obligatorio");
        Objects.requireNonNull(rol, "El rol del actor es obligatorio");
        Objects.requireNonNull(almacenId, "El almacenId del actor es obligatorio");
    }
}
