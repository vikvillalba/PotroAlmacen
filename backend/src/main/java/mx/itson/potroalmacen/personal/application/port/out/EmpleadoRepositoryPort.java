package mx.itson.potroalmacen.personal.application.port.out;

import java.util.List;
import java.util.Optional;
import mx.itson.potroalmacen.personal.domain.Empleado;

/** Puerto de salida para la persistencia de empleados. */
public interface EmpleadoRepositoryPort {

    /**
     * Busca un empleado por su id.
     *
     * @param id id del empleado.
     * @return el empleado, o vacío si no existe.
     */
    Optional<Empleado> buscarPorId(Integer id);

    /**
     * Lista todos los empleados registrados.
     *
     * @return lista de empleados.
     */
    List<Empleado> listarTodos();
}
