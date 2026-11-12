package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataMaterialRepository extends JpaRepository<MaterialEntity, Long>, JpaSpecificationExecutor<MaterialEntity> {
    Optional<MaterialEntity> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
