package mx.itson.potroalmacen.salidas.adapter.out.eventos;

import java.util.Objects;
import mx.itson.potroalmacen.salidas.application.port.out.PublicadorEventosPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Implementa el publicador con {@link ApplicationEventPublisher}: Spring entrega el evento a cada bean que lo escuche
 * (por ejemplo, el listener de Auditoría para los eventos auditables). Si no hay suscriptores, el evento se descarta.
 *
 * <p>El bean tiene nombre propio porque Materiales ya tiene un adaptador con el mismo nombre de clase.
 */
@Component("salidasPublicadorEventosAdapter")
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
