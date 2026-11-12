package mx.itson.potroalmacen.materiales.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SpringDataMaterialRepository extends JpaRepository<MaterialEntity, Long> {
    Optional<MaterialEntity> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
    
     @Query("""
            SELECT m FROM MaterialEntity m
            WHERE (:patron IS NULL
                   OR LOWER(m.nombre) LIKE :patron
                   OR LOWER(m.codigo) LIKE :patron)
              AND (:categoria IS NULL OR m.categoria = :categoria)
              AND (:campus IS NULL OR m.campus = :campus)
            ORDER BY m.nombre
            """)
    List<MaterialEntity> buscar(@Param("patron") String patron,
                                @Param("categoria") Categoria categoria,
                                @Param("campus") Campus campus);
}
