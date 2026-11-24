package mx.itson.potroalmacen.salidas.adapter.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import mx.itson.potroalmacen.compartido.web.GlobalExceptionHandler;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitarEntregaUseCase;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand;
import mx.itson.potroalmacen.salidas.domain.Boleta;
import mx.itson.potroalmacen.salidas.domain.EmpleadoRef;
import mx.itson.potroalmacen.salidas.domain.EstadoBoleta;
import mx.itson.potroalmacen.salidas.domain.MaterialRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class EntregaMaterialEmpleadoControllerTest {

    private static final String SOLICITUD = """
            {
              "idEntregador": 2,
              "materiales": [
                { "codigoMaterial": "001", "cantidadAEntregar": 2 }
              ]
            }
            """;

    private SolicitudEntregaCommand comandoRegistrado;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SolicitarEntregaUseCase solicitarEntrega = comando -> {
            comandoRegistrado = comando;
            return Boleta.reconstruir(
                    12,
                    "BOL-TEST012",
                    new EmpleadoRef(comando.idSolicitante(), "María López", "Intendente"),
                    new EmpleadoRef(comando.idEntregador(), "José Ramírez", "Almacenista"),
                    LocalDateTime.of(2026, 11, 24, 10, 0),
                    EstadoBoleta.COMPLETADA,
                    List.of(new MaterialRef("001", "Limpiador multiusos", 2)));
        };

        mockMvc = MockMvcBuilders.standaloneSetup(new EntregaMaterialEmpleadoController(solicitarEntrega))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void solicitarEntregaUsaElEmpleadoDeLaRuta() throws Exception {
        mockMvc.perform(post("/api/salidas/empleados/1/entregas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SOLICITUD))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.idSolicitante").value(1))
                .andExpect(jsonPath("$.idEntregador").value(2))
                .andExpect(jsonPath("$.materiales[0].codigoMaterial").value("001"));

        assertEquals(1, comandoRegistrado.idSolicitante());
        assertEquals(2, comandoRegistrado.idEntregador());
        assertEquals(List.of(new SolicitudEntregaCommand.MaterialSolicitado("001", 2)), comandoRegistrado.materiales());
    }

    @Test
    void solicitudSinMateriales() throws Exception {
        mockMvc.perform(post("/api/salidas/empleados/1/entregas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEntregador\":2,\"materiales\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("VALIDACION"));

        assertNull(comandoRegistrado);
    }
}
