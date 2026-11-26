package mx.itson.potroalmacen.personal.application.port.out;

import java.util.Optional;
import mx.itson.potroalmacen.personal.domain.Empleado;

/** Puerto de salida para consultar empleados con bloqueo pesimista en base de datos. */
public interface EmpleadoRepositoryConcurrenciaPort {

    /**
     * Busca un empleado por su id bloqueando su fila en la base de datos para actualización concurrente.
     *
     * @param id id del empleado.
     * @return el empleado, o vacío si no existe.
     */
    Optional<Empleado> buscarPorIdConBloqueo(Integer id);
}
