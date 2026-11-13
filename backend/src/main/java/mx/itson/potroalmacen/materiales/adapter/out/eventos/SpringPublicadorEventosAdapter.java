package mx.itson.potroalmacen.materiales.adapter.out.eventos;

import java.util.Objects;
import mx.itson.potroalmacen.materiales.application.port.out.PublicadorEventosPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Implementa el publicador con {@link ApplicationEventPublisher}: Spring entrega el evento a cada bean que lo escuche
 * ({@code @ApplicationModuleListener} o {@code @EventListener}). Si no hay suscriptores, el evento se descarta.
 */
@Component
class SpringPublicadorEventosAdapter implements PublicadorEventosPort {

    private final ApplicationEventPublisher publisher;

    SpringPublicadorEventosAdapter(ApplicationEventPublisher publisher) {
        this.publisher = Objects.requireNonNull(publisher, "publisher es obligatorio");
    }

    @Override
    public void publicar(Object evento) {
        publisher.publishEvent(Objects.requireNonNull(evento, "El evento no puede ser nulo"));
    }
}
