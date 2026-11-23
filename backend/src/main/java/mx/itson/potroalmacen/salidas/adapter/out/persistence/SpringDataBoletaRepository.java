package mx.itson.potroalmacen.salidas.adapter.out.persistence;

import java.time.LocalDateTime;
import mx.itson.potroalmacen.salidas.domain.EstadoBoleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataBoletaRepository extends JpaRepository<BoletaEntity, Long> {

    @Query("""
            SELECT COALESCE(SUM(d.cantidad), 0)
            FROM BoletaDetalleEntity d
            WHERE d.boleta.solicitanteId = :solicitanteId
              AND d.codigoMaterial = :codigoMaterial
              AND d.boleta.estado = :estado
              AND d.boleta.fechaHora >= :desde
            """)
    long sumarCantidadEntregada(
            @Param("solicitanteId") Integer solicitanteId,
            @Param("codigoMaterial") String codigoMaterial,
            @Param("estado") EstadoBoleta estado,
            @Param("desde") LocalDateTime desde);
}
