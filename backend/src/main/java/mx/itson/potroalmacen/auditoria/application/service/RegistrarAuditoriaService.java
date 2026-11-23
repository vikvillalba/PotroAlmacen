package mx.itson.potroalmacen.auditoria.application.service;

import java.util.Objects;
import mx.itson.potroalmacen.auditoria.application.port.in.RegistrarAuditoriaUseCase;
import mx.itson.potroalmacen.auditoria.application.port.out.RegistroAuditoriaRepositoryPort;
import mx.itson.potroalmacen.auditoria.domain.RegistroAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.EventoAuditable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del caso de uso {@link RegistrarAuditoriaUseCase}.
 * Convierte el evento en un registro de la bitácora (el dominio valida los datos) y lo guarda.
 */
@Service
@Transactional
public class RegistrarAuditoriaService implements RegistrarAuditoriaUseCase {

    private final RegistroAuditoriaRepositoryPort registroAuditoriaRepository;

    public RegistrarAuditoriaService(RegistroAuditoriaRepositoryPort registroAuditoriaRepository) {
        this.registroAuditoriaRepository =
                Objects.requireNonNull(registroAuditoriaRepository, "registroAuditoriaRepository es obligatorio");
    }

    @Override
    public RegistroAuditoria registrar(EventoAuditable evento) {
        RegistroAuditoria registro = RegistroAuditoria.desdeEvento(evento);
        return registroAuditoriaRepository.guardar(registro);
    }
}
