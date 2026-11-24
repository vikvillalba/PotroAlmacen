package mx.itson.potroalmacen.salidas.adapter.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mx.itson.potroalmacen.areas.application.port.in.MaterialPermitidoResumen;
import mx.itson.potroalmacen.areas.application.port.in.SolicitudMaterialArea;
import mx.itson.potroalmacen.areas.application.port.in.ValidarMaterialAreaUseCase;
import mx.itson.potroalmacen.areas.domain.exceptions.MaterialNoPermitidoEnAreaException;
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

class EntregaMaterialAreaControllerTest {

    private static final String SOLICITUD = """
            {
              "idSolicitante": 1,
              "idEntregador": 2,
              "materiales": [
                { "codigoMaterial": "001", "cantidadAEntregar": 1 }
              ]
            }
            """;

    private final List<SolicitudMaterialArea> solicitudesArea = new ArrayList<>();
    private SolicitudEntregaCommand comandoRegistrado;
    private RuntimeException errorValidacion;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ValidarMaterialAreaUseCase validarArea = new ValidarMaterialAreaUseCase() {
            @Override
            public List<MaterialPermitidoResumen> consultarMaterialesPermitidos(Integer idArea) {
                return List.of();
            }

            @Override
            public void validar(SolicitudMaterialArea solicitud) {
                solicitudesArea.add(solicitud);
                if (errorValidacion != null) {
                    throw errorValidacion;
                }
            }
        };

        SolicitarEntregaUseCase solicitarEntrega = comando -> {
            comandoRegistrado = comando;
            return Boleta.reconstruir(
                    10,
                    "BOL-TEST001",
                    new EmpleadoRef(1, "María López", "Intendente"),
                    new EmpleadoRef(2, "José Ramírez", "Almacenista"),
                    LocalDateTime.of(2026, 11, 24, 10, 0),
                    EstadoBoleta.COMPLETADA,
                    List.of(new MaterialRef("001", "Limpiador multiusos", 1)));
        };

        mockMvc = MockMvcBuilders.standaloneSetup(new EntregaMaterialAreaController(validarArea, solicitarEntrega))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void solicitarEntregaValidaMaterialDelArea() throws Exception {
        mockMvc.perform(post("/api/salidas/areas/1/entregas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SOLICITUD))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.estado").value("COMPLETADA"))
                .andExpect(jsonPath("$.materiales[0].codigoMaterial").value("001"));

        assertEquals(List.of(new SolicitudMaterialArea(1, "001", 1)), solicitudesArea);
        assertEquals(1, comandoRegistrado.idSolicitante());
        assertEquals(2, comandoRegistrado.idEntregador());
        assertEquals(List.of(new SolicitudEntregaCommand.MaterialSolicitado("001", 1)), comandoRegistrado.materiales());
    }

    @Test
    void solicitarEntregaConMaterialNoPermitido() throws Exception {
        errorValidacion = new MaterialNoPermitidoEnAreaException("022");
        String solicitudNoPermitida = SOLICITUD.replace("001", "022");

        mockMvc.perform(post("/api/salidas/areas/1/entregas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(solicitudNoPermitida))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("VALIDACION"));

        assertEquals(List.of(new SolicitudMaterialArea(1, "022", 1)), solicitudesArea);
        assertNull(comandoRegistrado);
    }
}
