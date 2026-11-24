package mx.itson.potroalmacen.salidas.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Objects;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitarEntregaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** HTTP API para solicitar la entrega de materiales a un empleado determinado. */
@RestController
@RequestMapping("/api/salidas/empleados/{idEmpleado}/entregas")
@Tag(name = "Salidas", description = "Solicitudes de entrega de material a empleados")
class EntregaMaterialEmpleadoController {

    private final SolicitarEntregaUseCase solicitarEntrega;

    EntregaMaterialEmpleadoController(SolicitarEntregaUseCase solicitarEntrega) {
        this.solicitarEntrega = Objects.requireNonNull(solicitarEntrega);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Solicita la entrega de materiales a un empleado")
    BoletaResponse solicitarEntrega(
            @PathVariable Integer idEmpleado, @Valid @RequestBody EntregaMaterialEmpleadoRequest solicitud) {
        return BoletaResponse.fromDomain(solicitarEntrega.solicitar(solicitud.toCommand(idEmpleado)));
    }
}
