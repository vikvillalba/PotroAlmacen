package mx.itson.potroalmacen.personal.adapter.out.persistence;

import mx.itson.potroalmacen.personal.domain.AreaRef;
import mx.itson.potroalmacen.personal.domain.Empleado;
import org.springframework.stereotype.Component;

/** Convierte {@link EmpleadoEntity} al agregado {@link Empleado}. */
@Component
public class EmpleadoMapper {

    public Empleado toDomain(EmpleadoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Empleado.crear(
                        entity.getNombre(),
                        entity.getApellidoPaterno(),
                        entity.getApellidoMaterno(),
                        entity.getTipoEmpleado(),
                        entity.getPuesto(),
                        entity.getJefeDirecto(),
                        entity.getTelefono(),
                        entity.getAreasAsignadas().stream()
                                .map(area -> new AreaRef(area.getAreaId(), area.getNombreArea()))
                                .toList())
                .reconstitadir(Math.toIntExact(entity.getId()), entity.isActivo(), entity.isEnPrestamo());
    }
}
