package mx.itson.potroalmacen.areas.adapter.out.persistence;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import mx.itson.potroalmacen.areas.application.port.out.AreaRepositoryPort;
import mx.itson.potroalmacen.areas.domain.Area;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adaptador de salida de persistencia para el módulo de áreas.
 *
 * <p>Implementa {@link AreaRepositoryPort} traduciendo las operaciones del dominio a consultas y
 * escrituras JPA mediante {@link SpringDataAreaRepository} y {@link AreaPersistenceMapper}.
 */
@Repository
class AreaPersistenceAdapter implements AreaRepositoryPort {

    private final SpringDataAreaRepository areaRepository;
    private final AreaPersistenceMapper mapper;

    AreaPersistenceAdapter(SpringDataAreaRepository areaRepository, AreaPersistenceMapper mapper) {
        this.areaRepository = Objects.requireNonNull(areaRepository, "areaRepository es obligatorio");
        this.mapper = Objects.requireNonNull(mapper, "mapper es obligatorio");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Area> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return areaRepository.findById(id.longValue()).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Area> listarTodas() {
        return areaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Area guardar(Area area) {
        Objects.requireNonNull(area, "area es obligatoria");
        AreaEntity nueva = mapper.toEntity(area);
        AreaEntity entidad;

        if (nueva.getId() != null) {
            entidad = areaRepository.findById(nueva.getId()).orElse(nueva);
            if (entidad != nueva) {
                entidad.setNombreArea(nueva.getNombreArea());
                entidad.setTipoArea(nueva.getTipoArea());
                entidad.setMetrosCuadrados(nueva.getMetrosCuadrados());

                var codigosActuales = entidad.getMateriales().stream()
                        .map(AreaMaterialEntity::getCodigoMaterial)
                        .collect(Collectors.toSet());

                nueva.getMateriales().stream()
                        .filter(m -> !codigosActuales.contains(m.getCodigoMaterial()))
                        .forEach(entidad::agregarMaterial);
            }
        } else {
            entidad = nueva;
        }

        AreaEntity guardada = areaRepository.save(entidad);
        return mapper.toDomain(guardada);
    }
}
