package mx.itson.potroalmacen.salidas.adapter.out.persistence;

import mx.itson.potroalmacen.salidas.application.port.out.BoletaRepositoryPort;
import mx.itson.potroalmacen.salidas.domain.Boleta;
import org.springframework.stereotype.Repository;

@Repository
public class BoletaPersistenceAdapter implements BoletaRepositoryPort {

    private final SpringDataBoletaRepository repository;
    private final BoletaMapper mapper;

    public BoletaPersistenceAdapter(SpringDataBoletaRepository repository, BoletaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Boleta guardar(Boleta boleta) {
        BoletaEntity entity = mapper.toBoletaEntity(boleta);
        entity = repository.save(entity);
        return mapper.toDomain(entity);
    }
}
