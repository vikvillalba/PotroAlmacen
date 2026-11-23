package mx.itson.potroalmacen.auditoria.adapter.in.eventos;

import java.util.Objects;
import mx.itson.potroalmacen.auditoria.application.port.in.RegistrarAuditoriaUseCase;
import mx.itson.potroalmacen.auditoria.domain.exceptions.AuditoriaInvalidaException;
import mx.itson.potroalmacen.compartido.auditoria.EventoAuditable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Recibe cualquier evento que implemente {@link EventoAuditable}, sin importar el módulo que lo publique, y lo registra
 * en la bitácora con {@link RegistrarAuditoriaUseCase}.
 *
 * <p>Se ejecuta solo después de que la transacción que publicó el evento hace commit ({@code
 * @TransactionalEventListener}) y en una transacción propia ({@code REQUIRES_NEW}): si la auditoría falla, la
 * operación original no se revierte. Es lo mismo que hace {@code @ApplicationModuleListener} de Spring Modulith (sin
 * {@code @Async}), que no se usa porque requiere la dependencia {@code spring-modulith-events-api}.
 */
@Component
public class EventoAuditableListener {

    private static final Logger log = LoggerFactory.getLogger(EventoAuditableListener.class);

    private final RegistrarAuditoriaUseCase registrarAuditoria;

    public EventoAuditableListener(RegistrarAuditoriaUseCase registrarAuditoria) {
        this.registrarAuditoria = Objects.requireNonNull(registrarAuditoria, "registrarAuditoria es obligatorio");
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void alOcurrirEventoAuditable(EventoAuditable evento) {
        try {
            registrarAuditoria.registrar(evento);
        } catch (AuditoriaInvalidaException e) {
            // El evento ya ocurrió y no se puede corregir aquí; se deja constancia en el log
            // para que la operación de negocio que lo publicó no falle por la bitácora.
            log.warn("No se registró en la bitácora el evento {}: {}", evento, e.getMessage());
        }
    }
}
