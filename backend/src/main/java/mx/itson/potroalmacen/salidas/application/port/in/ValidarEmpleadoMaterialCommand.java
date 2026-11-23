package mx.itson.potroalmacen.salidas.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que modela la validación de la solicitud de un material para un determinado empleado.
 *
 * @param idSolicitante Id del empleado que solicita los materiales.
 * @param materiales Materiales a validar.
 * @param fechaReferencia Fecha de referencia opcional para calcular la ventana de días del límite (por defecto la fecha actual).
 */
public record ValidarEmpleadoMaterialCommand(
        Integer idSolicitante,
        List<SolicitudEntregaCommand.MaterialSolicitado> materiales,
        LocalDateTime fechaReferencia) {

    public ValidarEmpleadoMaterialCommand(
            Integer idSolicitante, List<SolicitudEntregaCommand.MaterialSolicitado> materiales) {
        this(idSolicitante, materiales, null);
    }

    public ValidarEmpleadoMaterialCommand {
        materiales = (materiales == null) ? List.of() : List.copyOf(materiales);
    }
}
