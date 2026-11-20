package mx.itson.potroalmacen.salidas.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand.MaterialSolicitado;

public record SolicitarEntregaRequest(
        @NotNull Integer idSolicitante,
        @NotNull Integer idEntregador,
        @NotEmpty List<@Valid MaterialSolicitadoRequest> materiales) {

    public record MaterialSolicitadoRequest(
            @NotBlank String codigoMaterial,
            @NotNull @Positive Integer cantidadAEntregar) {

        public MaterialSolicitado toCommand() {
            return new MaterialSolicitado(codigoMaterial, cantidadAEntregar);
        }
    }

    public SolicitudEntregaCommand toCommand() {
        List<MaterialSolicitado> lineas = materiales == null
                ? List.of()
                : materiales.stream().map(MaterialSolicitadoRequest::toCommand).toList();
        return new SolicitudEntregaCommand(idSolicitante, idEntregador, lineas);
    }
}
