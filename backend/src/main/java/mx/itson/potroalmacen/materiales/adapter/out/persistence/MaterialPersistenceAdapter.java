package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.FiltroMateriales;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.exceptions.CodigoDuplicadoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class MaterialPersistenceAdapter implements MaterialRepositoryPort {

    private static final Sort POR_NOMBRE = Sort.by("nombre").and(Sort.by("codigo"));

    private final SpringDataMaterialRepository repository;
    private final MaterialMapper mapper;

    public MaterialPersistenceAdapter(SpringDataMaterialRepository repository, MaterialMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Material guardar(Material material) {
        MaterialEntity entity = mapper.toEntity(material);
        try {
            // saveAndFlush para que la violación del UNIQUE de código salga aquí y no al hacer commit.
            MaterialEntity savedEntity = repository.saveAndFlush(entity);
            return mapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException ex) {
            // El único UNIQUE de mat_material es el código: otro registro lo insertó entre la validación y el guardado.
            throw new CodigoDuplicadoException(material.getCodigo().toString());
        }
    }

    @Override
    public Optional<Material> buscarPorCodigo(CodigoMaterial codigo) {
        if (codigo == null) return Optional.empty();
        return repository.findByCodigo(codigo.toString()).map(mapper::toDomain);
    }

    @Override
    public boolean existePorCodigo(CodigoMaterial codigo) {
        if (codigo == null) return false;
        return repository.existsByCodigo(codigo.toString());
    }

    @Override
    public List<Material> buscarTodos() {
        return repository.findAll(POR_NOMBRE).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Material> buscar(FiltroMateriales filtro) {
        return repository.findAll(especificacion(filtro), POR_NOMBRE).stream()
                .map(mapper::toDomain)
                .toList();
    }

    /** Traduce el filtro a SQL. Debe coincidir con {@link FiltroMateriales#aplicaA}. */
    private static Specification<MaterialEntity> especificacion(FiltroMateriales filtro) {
        return (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();
            if (filtro.nombre() != null) {
                condiciones.add(cb.like(cb.lower(root.get("nombre")), patron(filtro.nombre()), '\\'));
            }
            if (filtro.codigo() != null) {
                condiciones.add(cb.like(cb.lower(root.get("codigo")), patron(filtro.codigo()), '\\'));
            }
            if (filtro.categoria() != null) {
                condiciones.add(cb.equal(root.get("categoria"), filtro.categoria()));
            }
            if (filtro.presentacion() != null) {
                condiciones.add(cb.equal(root.get("presentacion"), filtro.presentacion()));
            }
            return cb.and(condiciones.toArray(Predicate[]::new));
        };
    }

    /** Patrón "contiene" para LIKE, en minúsculas. %, _ y \ escritos por el usuario se buscan literalmente. */
    private static String patron(String texto) {
        return "%" + escaparLike(texto.toLowerCase()) + "%";
    }

    private static String escaparLike(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
