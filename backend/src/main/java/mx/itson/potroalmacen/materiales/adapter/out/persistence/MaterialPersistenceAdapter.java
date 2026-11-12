package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import java.util.Optional;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
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
        MaterialEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
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
}
