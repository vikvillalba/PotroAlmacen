package mx.itson.potroalmacen.personal.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataEmpleadoRepository extends JpaRepository<EmpleadoEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EmpleadoEntity e WHERE e.id = :id")
    Optional<EmpleadoEntity> findByIdConBloqueo(@Param("id") Long id);
}
