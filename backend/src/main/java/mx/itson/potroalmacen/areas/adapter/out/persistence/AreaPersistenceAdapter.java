package mx.itson.potroalmacen.areas.adapter.out.persistence;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    /**
     * Construye el adaptador con sus dependencias de acceso a datos y mapeo.
     *
     * @param areaRepository repositorio Spring Data JPA para {@link AreaEntity}.
     * @param mapper conversor entre entidades JPA y el agregado de dominio {@link Area}.
     */
    AreaPersistenceAdapter(SpringDataAreaRepository areaRepository, AreaPersistenceMapper mapper) {
        this.areaRepository = Objects.requireNonNull(areaRepository, "areaRepository es obligatorio");
        this.mapper = Objects.requireNonNull(mapper, "mapper es obligatorio");
    }

    /**
     * Busca un área por su identificador y la transforma al modelo de dominio.
     *
     * <p>Se ejecuta en una transacción de solo lectura para garantizar que las colecciones hijas
     * ({@code espacios} y {@code materiales}) puedan leerse sin problemas de carga perezosa.
     *
     * @param id identificador del área a buscar.
     * @return el agregado {@link Area} encontrado, o {@link Optional#empty()} si el {@code id} es
     *     nulo o no existe en base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Area> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        // El agregado usa Integer y la entidad JPA usa Long como clave primaria.
        return areaRepository.findById(id.longValue()).map(mapper::toDomain);
    }

    /**
     * Recupera todas las áreas registradas en base de datos y las convierte a dominio.
     *
     * @return lista inmutable con las áreas encontradas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Area> listarTodas() {
        return areaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    /**
     * Persiste un agregado {@link Area} en base de datos y devuelve su representación actualizada.
     *
     * @param area agregado de dominio a guardar; no puede ser nulo.
     * @return el agregado {@link Area} reconstruido tras la operación de guardado.
     */
    @Override
    @Transactional
    public Area guardar(Area area) {
        Objects.requireNonNull(area, "area es obligatoria");
        AreaEntity entidad = mapper.toEntity(area);
        AreaEntity guardada = areaRepository.save(entidad);
        return mapper.toDomain(guardada);
    }
}
