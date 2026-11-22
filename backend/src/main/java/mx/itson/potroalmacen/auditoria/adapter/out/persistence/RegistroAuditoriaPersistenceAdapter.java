package mx.itson.potroalmacen.auditoria.adapter.out.persistence;

import mx.itson.potroalmacen.auditoria.application.port.out.RegistroAuditoriaRepositoryPort;
import mx.itson.potroalmacen.auditoria.domain.RegistroAuditoria;
import org.springframework.stereotype.Repository;

@Repository
public class RegistroAuditoriaPersistenceAdapter implements RegistroAuditoriaRepositoryPort {

    private final SpringDataRegistroAuditoriaRepository repository;
    private final RegistroAuditoriaMapper mapper;

    public RegistroAuditoriaPersistenceAdapter(
            SpringDataRegistroAuditoriaRepository repository, RegistroAuditoriaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public RegistroAuditoria guardar(RegistroAuditoria registro) {
        RegistroAuditoriaEntity entity = repository.save(mapper.toEntity(registro));
        return mapper.toDomain(entity);
    }
}
