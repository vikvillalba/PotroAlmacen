package mx.itson.potroalmacen.personal.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Objects;
import mx.itson.potroalmacen.personal.application.port.in.ConsultarEmpleadoUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empleados")
@Tag(name = "Personal", description = "Consulta de empleados solicitantes y personal de almacén")
class EmpleadoController {

    private final ConsultarEmpleadoUseCase consultarEmpleado;

    EmpleadoController(ConsultarEmpleadoUseCase consultarEmpleado) {
        this.consultarEmpleado = Objects.requireNonNull(consultarEmpleado);
    }

    @GetMapping
    @Operation(summary = "Lista todo el personal registrado")
    List<EmpleadoResponse> listarTodos() {
        return consultarEmpleado.listarTodos().stream()
                .map(EmpleadoResponse::de)
                .toList();
    }

    @GetMapping("/almacen")
    @Operation(summary = "Lista los empleados activos del almacén")
    List<EmpleadoResponse> listarAlmacen() {
        return consultarEmpleado.listarAlmacen().stream()
                .map(EmpleadoResponse::de)
                .toList();
    }
}
