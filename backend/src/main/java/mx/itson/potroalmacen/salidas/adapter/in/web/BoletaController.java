package mx.itson.potroalmacen.salidas.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import mx.itson.potroalmacen.salidas.application.port.in.ConsultarBoletasUseCase;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitarEntregaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final ConsultarBoletasUseCase consultarBoletas;

    BoletaController(SolicitarEntregaUseCase solicitarEntrega, ConsultarBoletasUseCase consultarBoletas) {
        this.solicitarEntrega = Objects.requireNonNull(solicitarEntrega);
        this.consultarBoletas = Objects.requireNonNull(consultarBoletas);
    }

    @GetMapping
    @Operation(summary = "Lista todas las boletas de entrega registradas")
    List<BoletaResponse> listar() {
        return consultarBoletas.listar().stream()
                .map(BoletaResponse::fromDomain)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene el detalle de una boleta por su id")
    BoletaResponse detalle(@PathVariable Integer id) {
        return BoletaResponse.fromDomain(consultarBoletas.obtenerPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra la entrega de materiales y genera una boleta")
    BoletaResponse solicitarEntrega(@Valid @RequestBody SolicitarEntregaRequest solicitud) {
        return BoletaResponse.fromDomain(solicitarEntrega.solicitar(solicitud.toCommand()));
    }
}
