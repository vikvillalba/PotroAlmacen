package mx.itson.potroalmacen.personal.application.port.in;

import java.util.List;
import java.util.Optional;
import mx.itson.potroalmacen.personal.domain.Empleado;

/**
 * Puerto de entrada para consultar empleados.
 *
 * <p>Otros módulos (por ejemplo, Salidas al registrar una boleta) lo usan para obtener los datos de un empleado.
 */
public interface ConsultarEmpleadoUseCase {

    /**
     * Busca un empleado por su id.
     *
     * @param id id del empleado.
     * @return los datos básicos del empleado, o vacío si no existe.
     */
    Optional<EmpleadoResumen> buscarPorId(Integer id);

    List<Empleado> listarTodos();

    List<Empleado> listarAlmacen();
}
