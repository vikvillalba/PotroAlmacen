package mx.itson.potroalmacen.materiales.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import mx.itson.potroalmacen.materiales.application.port.in.ConsultarMaterialesUseCase;
import mx.itson.potroalmacen.materiales.application.port.in.FiltroMateriales;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarMaterialUseCase;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API del catálogo de materiales: registro, consulta y opciones de los catálogos.
 *
 * <p>Solo traduce HTTP a los puertos de entrada; la lógica vive en los casos de uso. Los errores los responde
 * {@code GlobalExceptionHandler}: 400 si un dato es inválido y 409 si el código ya existe.
 */
@RestController
@RequestMapping("/api/materiales")
@Tag(name = "Materiales", description = "Registro y consulta del catálogo de materiales")
class MaterialController {

    private final RegistrarMaterialUseCase registrarMaterial;
    private final ConsultarMaterialesUseCase consultarMateriales;

    MaterialController(RegistrarMaterialUseCase registrarMaterial, ConsultarMaterialesUseCase consultarMateriales) {
        this.registrarMaterial = Objects.requireNonNull(registrarMaterial);
        this.consultarMateriales = Objects.requireNonNull(consultarMateriales);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un material con su existencia inicial")
    MaterialResumenResponse registrar(@Valid @RequestBody RegistrarMaterialRequest solicitud) {
        return MaterialResumenResponse.de(registrarMaterial.registrar(solicitud.aComando()));
    }

    @GetMapping
    @Operation(
            summary = "Lista materiales",
            description = "Sin parámetros devuelve todos. busqueda coincide parcialmente con el nombre o el código,"
                    + " sin distinguir mayúsculas; los parámetros que se envíen se combinan con Y.")
    List<MaterialResumenResponse> listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) Campus campus) {
        return consultarMateriales.listar(new FiltroMateriales(busqueda, categoria, campus)).stream()
                .map(MaterialResumenResponse::de)
                .toList();
    }

    @GetMapping("/catalogos")
    @Operation(summary = "Opciones de categorías, campus, unidades de medida y presentaciones")
    CatalogosResponse catalogos() {
        return new CatalogosResponse(
                Arrays.stream(Categoria.values())
                        .map(c -> OpcionCatalogo.de(c, c.getEtiqueta()))
                        .toList(),
                Arrays.stream(Campus.values())
                        .map(c -> OpcionCatalogo.de(c, c.getEtiqueta()))
                        .toList(),
                Arrays.stream(UnidadMedida.values())
                        .map(u -> OpcionCatalogo.de(u, u.getEtiqueta()))
                        .toList(),
                Arrays.stream(Presentacion.values())
                        .map(p -> OpcionCatalogo.de(p, p.getEtiqueta()))
                        .toList());
    }
}
