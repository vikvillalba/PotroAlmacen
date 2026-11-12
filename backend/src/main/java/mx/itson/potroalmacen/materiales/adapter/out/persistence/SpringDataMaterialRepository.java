package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMaterialRepository extends JpaRepository<MaterialEntity, Long> {
    Optional<MaterialEntity> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
