package mx.itson.potroalmacen.salidas.application.port.out;

import java.util.Optional;
import mx.itson.potroalmacen.salidas.domain.EmpleadoRef;

/**
 * Puerto de salida para consultar empleados.
 *
 * <p>Los empleados son del módulo Personal: el adaptador que lo implemente los obtiene de ese módulo y los convierte
 * a la referencia que usa Salidas.
 */
public interface EmpleadoConsultaPort {

    /**
     * Busca un empleado por su id.
     *
     * @param idEmpleado id del empleado.
     * @return la referencia del empleado (id, nombre completo y puesto), o vacío si no existe.
     */
    Optional<EmpleadoRef> buscarPorId(Integer idEmpleado);
}
