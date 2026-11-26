package mx.itson.potroalmacen.salidas.application.port.out;

import java.util.Optional;
import mx.itson.potroalmacen.salidas.domain.EmpleadoRef;

/**
 * Puerto de salida para consultar un empleado adquiriendo un bloqueo pesimista en base de datos.
 * Utilizado para serializar entregas concurrentes de un mismo solicitante.
 */
public interface EmpleadoConsultaConcurrenciaPort {

    /**
     * Busca un empleado por su id bloqueando el registro en base de datos para actualización concurrente.
     *
     * @param idEmpleado id del empleado.
     * @return la referencia del empleado, o vacío si no existe.
     */
    Optional<EmpleadoRef> buscarPorIdConBloqueo(Integer idEmpleado);
}
