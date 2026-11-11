package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataMaterialRepository extends JpaRepository<MaterialEntity, Long> {
    Optional<MaterialEntity> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}