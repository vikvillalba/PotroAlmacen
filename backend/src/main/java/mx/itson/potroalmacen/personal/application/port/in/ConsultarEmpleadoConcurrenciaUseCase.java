package mx.itson.potroalmacen.personal.application.port.in;

import java.util.Optional;

/**
 * Puerto de entrada para consultar empleados adquiriendo un bloqueo pesimista en base de datos.
 * Se utiliza en flujos transaccionales concurrentes que requieren serializar operaciones por empleado.
 */
public interface ConsultarEmpleadoConcurrenciaUseCase {

    /**
     * Busca un empleado por su id bloqueando su registro en la base de datos hasta fin de transacción.
     *
     * @param id id del empleado.
     * @return los datos básicos del empleado, o vacío si no existe.
     */
    Optional<EmpleadoResumen> buscarPorIdConBloqueo(Integer id);
}
