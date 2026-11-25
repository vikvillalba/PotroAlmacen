package mx.itson.potroalmacen.areas.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import mx.itson.potroalmacen.areas.application.port.in.AsignarMaterialAreaCommand;
import mx.itson.potroalmacen.areas.application.port.in.AsignarMaterialAreaUseCase;
import mx.itson.potroalmacen.areas.application.port.in.ConsultarAreasUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/areas")
@Tag(name = "Áreas", description = "Consulta de áreas y asignación de materiales")
class AreaController {

    private final ConsultarAreasUseCase consultarAreas;
    private final AsignarMaterialAreaUseCase asignarMaterialArea;

    AreaController(ConsultarAreasUseCase consultarAreas, AsignarMaterialAreaUseCase asignarMaterialArea) {
        this.consultarAreas = Objects.requireNonNull(consultarAreas);
        this.asignarMaterialArea = Objects.requireNonNull(asignarMaterialArea);
    }

    @GetMapping
    @Operation(summary = "Lista todas las áreas registradas")
    List<AreaResumenResponse> listar() {
        return consultarAreas.listarTodas().stream()
                .map(AreaResumenResponse::de)
                .toList();
    }

    @PostMapping("/{idArea}/materiales")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Asocia un material con su cantidad asignada a un área")
    AreaResumenResponse asignarMaterial(
            @PathVariable Integer idArea, @Valid @RequestBody AsignarMaterialAreaRequest solicitud) {
        var area = asignarMaterialArea.asignar(new AsignarMaterialAreaCommand(
                idArea, solicitud.codigoMaterial(), solicitud.nombreMaterial(), solicitud.cantidad()));
        return AreaResumenResponse.de(area);
    }
}
