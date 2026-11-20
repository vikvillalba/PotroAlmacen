package mx.itson.potroalmacen.salidas.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Objects;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitarEntregaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API de salidas de almacén: registro de entregas de material y generación de boletas.
 *
 * <p>Solo traduce HTTP al puerto de entrada; la lógica vive en los casos de uso. Los errores los responde
 * {@code GlobalExceptionHandler}.
 */
@RestController
@RequestMapping("/api/salidas/boletas")
@Tag(name = "Salidas", description = "Registro de entregas de material y boletas")
class BoletaController {

    private final SolicitarEntregaUseCase solicitarEntrega;

    BoletaController(SolicitarEntregaUseCase solicitarEntrega) {
        this.solicitarEntrega = Objects.requireNonNull(solicitarEntrega);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra la entrega de materiales y genera una boleta")
    BoletaResponse solicitarEntrega(@Valid @RequestBody SolicitarEntregaRequest solicitud) {
        return BoletaResponse.fromDomain(solicitarEntrega.solicitar(solicitud.toCommand()));
    }
}
