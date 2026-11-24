package mx.itson.potroalmacen.salidas.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand;

/** Datos HTTP de una entrega dirigida al empleado indicado en la ruta. */
public record EntregaMaterialEmpleadoRequest(
        @NotNull Integer idEntregador,
        @NotEmpty List<SolicitarEntregaRequest.@Valid MaterialSolicitadoRequest> materiales) {

    public SolicitudEntregaCommand toCommand(Integer idEmpleado) {
        List<SolicitudEntregaCommand.MaterialSolicitado> lineas = materiales == null
                ? List.of()
                : materiales.stream()
                        .map(SolicitarEntregaRequest.MaterialSolicitadoRequest::toCommand)
                        .toList();
        return new SolicitudEntregaCommand(idEmpleado, idEntregador, lineas);
    }
}
