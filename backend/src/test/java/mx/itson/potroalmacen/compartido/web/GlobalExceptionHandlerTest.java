package mx.itson.potroalmacen.compartido.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import mx.itson.potroalmacen.compartido.excepcion.DuplicadoException;
import mx.itson.potroalmacen.compartido.excepcion.NoEncontradoException;
import mx.itson.potroalmacen.compartido.excepcion.PermisoDenegadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Cada tipo de error responde con su código HTTP y con el JSON acordado (código, mensaje y campos inválidos). */
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ControladorDePrueba())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void camposInvalidosResponden400ConLosCampos() throws Exception {
        mockMvc.perform(post("/prueba/validacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("VALIDACION"))
                .andExpect(jsonPath("$.mensaje").isNotEmpty())
                .andExpect(jsonPath("$.campos[0].campo").value("nombre"))
                .andExpect(jsonPath("$.campos[0].mensaje").isNotEmpty());
    }

    @Test
    void jsonMalFormadoResponde400() throws Exception {
        mockMvc.perform(post("/prueba/validacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"))
                .andExpect(jsonPath("$.campos").isEmpty());
    }

    @Test
    void permisoDenegadoResponde403() throws Exception {
        mockMvc.perform(get("/prueba/permiso"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("PERMISO_DENEGADO"))
                .andExpect(jsonPath("$.mensaje").value("Solo puedes operar en tu almacén."));
    }

    @Test
    void noEncontradoResponde404() throws Exception {
        mockMvc.perform(get("/prueba/no-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("NO_ENCONTRADO"))
                .andExpect(jsonPath("$.campos").isEmpty());
    }

    @Test
    void duplicadoResponde409ConElCodigoDelModulo() throws Exception {
        mockMvc.perform(get("/prueba/duplicado"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("PRUEBA_DUPLICADA"))
                .andExpect(jsonPath("$.mensaje").value("Ya existe un registro con la clave X."));
    }

    @Test
    void errorNoPrevistoResponde500SinDetallesInternos() throws Exception {
        mockMvc.perform(get("/prueba/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"))
                .andExpect(jsonPath("$.mensaje").value("Ocurrió un error inesperado. Intenta de nuevo."));
    }

    record SolicitudDePrueba(@NotBlank String nombre) {}

    @RestController
    @RequestMapping("/prueba")
    static class ControladorDePrueba {

        @PostMapping("/validacion")
        void validar(@Valid @RequestBody SolicitudDePrueba solicitud) {}

        @GetMapping("/permiso")
        void permiso() {
            throw new PermisoDenegadoException("Solo puedes operar en tu almacén.");
        }

        @GetMapping("/no-encontrado")
        void noEncontrado() {
            throw new NoEncontradoException("No existe el registro 99.");
        }

        @GetMapping("/duplicado")
        void duplicado() {
            throw new DuplicadoException("PRUEBA_DUPLICADA", "Ya existe un registro con la clave X.");
        }

        @GetMapping("/error")
        void error() {
            throw new IllegalStateException("detalle interno que no debe salir en la respuesta");
        }
    }
}
