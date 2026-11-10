package mx.itson.potroalmacen.seguridad.dev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import mx.itson.potroalmacen.seguridad.Actor;
import mx.itson.potroalmacen.seguridad.Rol;
import org.junit.jupiter.api.Test;

class ActorFijoResolverTest {

    @Test
    void devuelveSiempreElActorConfigurado() {
        ActorFijoProperties properties = new ActorFijoProperties(1L, Rol.ALMACENISTA, 1L);
        ActorFijoResolver resolver = new ActorFijoResolver(properties.toActor());

        Actor actor = resolver.resolve();

        assertThat(actor).isEqualTo(new Actor(1L, Rol.ALMACENISTA, 1L));
        assertThat(resolver.resolve()).isSameAs(actor);
    }

    @Test
    void fallaSiLaConfiguracionEstaIncompleta() {
        ActorFijoProperties properties = new ActorFijoProperties(1L, null, 1L);

        assertThatThrownBy(properties::toActor)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("rol");
    }
}
