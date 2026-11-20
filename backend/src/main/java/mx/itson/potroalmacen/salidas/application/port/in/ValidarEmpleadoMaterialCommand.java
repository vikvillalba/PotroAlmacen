package mx.itson.potroalmacen.salidas.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que modela la validación de la solicitud de un material para un determinado empleado.
 * @param idSolicitante Id del empleado que solicita los materiales
 * @param fechaEntrega Fecha de entrega de los materiales
 * @param materiales
 */
public record ValidarEmpleadoMaterialCommand(
        Integer idSolicitante,
        Integer idArea,
        LocalDateTime fechaEntrega,
        List<SolicitudEntregaCommand.MaterialSolicitado> materiales) {
    public ValidarEmpleadoMaterialCommand {
        materiales = List.copyOf(materiales);
    }
}
