package mx.itson.potroalmacen.salidas.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Objects;
import mx.itson.potroalmacen.areas.application.port.in.SolicitudMaterialArea;
import mx.itson.potroalmacen.areas.application.port.in.ValidarMaterialAreaUseCase;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitarEntregaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** HTTP API para solicitar la entrega de materiales destinada a un área. */
@RestController
@RequestMapping("/api/salidas/areas/{idArea}/entregas")
@Tag(name = "Salidas", description = "Registro de entregas de material para áreas")
class EntregaMaterialAreaController {

    private final ValidarMaterialAreaUseCase validarMaterialArea;
    private final SolicitarEntregaUseCase solicitarEntrega;

    EntregaMaterialAreaController(
            ValidarMaterialAreaUseCase validarMaterialArea, SolicitarEntregaUseCase solicitarEntrega) {
        this.validarMaterialArea = Objects.requireNonNull(validarMaterialArea);
        this.solicitarEntrega = Objects.requireNonNull(solicitarEntrega);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Solicita la entrega de materiales para un área")
    BoletaResponse solicitarEntrega(
            @PathVariable Integer idArea, @Valid @RequestBody SolicitarEntregaRequest solicitud) {
        solicitud
                .materiales()
                .forEach(material -> validarMaterialArea.validar(
                        new SolicitudMaterialArea(idArea, material.codigoMaterial(), material.cantidadAEntregar())));
        return BoletaResponse.fromDomain(solicitarEntrega.solicitar(solicitud.toCommand()));
    }
}
