package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.application.port.in.FiltroMateriales;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.exceptions.CodigoDuplicadoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class MaterialPersistenceAdapter implements MaterialRepositoryPort {

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
    public List<Material> buscar(FiltroMateriales filtro) {
        String patron =
                filtro.busqueda() == null ? null : "%" + filtro.busqueda().toLowerCase() + "%";
        return repository.buscar(patron, filtro.categoria(), filtro.campus()).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
