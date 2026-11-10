package mx.itson.potroalmacen.seguridad.dev;

import java.util.Objects;
import mx.itson.potroalmacen.seguridad.Actor;
import mx.itson.potroalmacen.seguridad.ActorResolver;

/** {@link ActorResolver} de desarrollo: siempre devuelve el mismo actor, sin autenticación. */
class ActorFijoResolver implements ActorResolver {

    private final Actor actor;

    ActorFijoResolver(Actor actor) {
        this.actor = Objects.requireNonNull(actor, "El actor fijo es obligatorio");
    }

    @Override
    public Actor resolve() {
        return actor;
    }
}
