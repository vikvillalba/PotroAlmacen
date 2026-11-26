package mx.itson.potroalmacen.salidas.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import mx.itson.potroalmacen.salidas.application.port.out.BoletaRepositoryPort;
import mx.itson.potroalmacen.salidas.domain.Boleta;
import mx.itson.potroalmacen.salidas.domain.EstadoBoleta;
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

    @Override
    public List<Boleta> listarTodas() {
        return repository.findAllByOrderByFechaHoraDesc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Boleta> buscarPorId(Integer id) {
        return repository.findById(id.longValue()).map(mapper::toDomain);
    }

    @Override
    public int cantidadEntregada(Integer solicitanteId, String codigoMaterial, LocalDateTime desde) {
        return Math.toIntExact(
                repository.sumarCantidadEntregada(solicitanteId, codigoMaterial, EstadoBoleta.COMPLETADA, desde));
    }
}
