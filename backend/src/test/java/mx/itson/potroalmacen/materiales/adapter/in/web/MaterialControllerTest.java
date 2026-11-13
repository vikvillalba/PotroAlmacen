package mx.itson.potroalmacen.materiales.adapter.in.web;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import mx.itson.potroalmacen.compartido.web.GlobalExceptionHandler;
import mx.itson.potroalmacen.materiales.application.port.in.ConsultarMaterialesUseCase;
import mx.itson.potroalmacen.materiales.application.port.in.FiltroMateriales;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarMaterialCommand;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarMaterialUseCase;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;
import mx.itson.potroalmacen.materiales.domain.exceptions.CodigoDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MaterialControllerTest {

    private static final String CUERPO_VALIDO = """
            {
              "codigo": "PIN-001",
              "nombre": "Pintura vinílica blanca",
              "categoria": "PINTURA",
              "unidadMedida": "LITRO",
              "presentacion": "CUBETA",
              "contenidoPresentacion": "19 L",
              "campus": "OBREGON_NAINARI",
              "precioUnitario": 450.00,
              "existenciaInicial": 10,
              "stockMinimo": 5,
              "stockMaximo": 50,
              "usoPrecauciones": "Mantener en lugar fresco"
            }
            """;

    private RegistrarMaterialCommand ultimoComando;
    private FiltroMateriales ultimaConsulta;
    private RuntimeException errorAlRegistrar;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RegistrarMaterialUseCase registrar = new RegistrarMaterialUseCase() {
            @Override
            public Material registrar(RegistrarMaterialCommand comando) {
                ultimoComando = comando;
                if (errorAlRegistrar != null) {
                    throw errorAlRegistrar;
                }
                Material material = material(comando.codigo(), comando.nombre()).conId(1);
                material.registrarEntrada(comando.existenciaInicial());
                return material;
            }
        };

        ConsultarMaterialesUseCase consultar = new ConsultarMaterialesUseCase() {
            @Override
            public List<Material> listar(FiltroMateriales filtro) {
                ultimaConsulta = filtro;
                return List.of(material("PIN-001", "Pintura vinílica blanca").conId(1));
            }

            @Override
            public Material obtenerPorId(Integer id) {
                if (id != null && id == 1) {
                    return material("PIN-001", "Pintura vinílica blanca").conId(1);
                }
                return null;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(new MaterialController(registrar, consultar))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registrarResponde201ConElMaterial() throws Exception {
        mockMvc.perform(post("/api/materiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("PIN-001"))
                .andExpect(jsonPath("$.cantidad").value(10))
                .andExpect(jsonPath("$.categoriaId").value("PINTURA"))
                .andExpect(jsonPath("$.categoria").value("Pintura"))
                .andExpect(jsonPath("$.campus").value("Obregón Náinari"))
                .andExpect(jsonPath("$.presentacion").value("Cubeta"))
                .andExpect(jsonPath("$.estado").value("Disponible"));

        assertEquals(Categoria.PINTURA, ultimoComando.categoria());
        assertEquals("19 L", ultimoComando.contenidoPresentacion());
        assertEquals(10, ultimoComando.existenciaInicial());
    }

    @Test
    void registrarSinCamposObligatoriosResponde400ConLosCampos() throws Exception {
        mockMvc.perform(post("/api/materiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("VALIDACION"))
                .andExpect(jsonPath("$.campos[?(@.campo == 'codigo')]").exists())
                .andExpect(jsonPath("$.campos[?(@.campo == 'categoria')]").exists())
                .andExpect(jsonPath("$.campos[?(@.campo == 'campus')]").exists());

        assertNull(ultimoComando);
    }

    @Test
    void registrarConCategoriaInexistenteResponde400() throws Exception {
        mockMvc.perform(post("/api/materiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO.replace("\"PINTURA\"", "\"NO_EXISTE\"")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrarCodigoDuplicadoResponde409() throws Exception {
        errorAlRegistrar = new CodigoDuplicadoException("PIN-001");

        mockMvc.perform(post("/api/materiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("MATERIAL_DUPLICADO"));
    }

    @Test
    void consultarSinParametrosPasaUnaConsultaVacia() throws Exception {
        mockMvc.perform(get("/api/materiales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("PIN-001"));

        assertEquals(FiltroMateriales.sinFiltros(), ultimaConsulta);
    }

    @Test
    void consultarPasaLosFiltros() throws Exception {
        mockMvc.perform(get("/api/materiales")
                        .param("busqueda", "pintura")
                        .param("categoria", "PINTURA")
                        .param("campus", "GUAYMAS"))
                .andExpect(status().isOk());

        assertEquals(new FiltroMateriales("pintura", Categoria.PINTURA, Campus.GUAYMAS), ultimaConsulta);
    }

    @Test
    void catalogosDevuelveIdYEtiqueta() throws Exception {
        mockMvc.perform(get("/api/materiales/catalogos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categorias[0].id").value("PINTURA"))
                .andExpect(jsonPath("$.categorias[0].nombre").value("Pintura"))
                .andExpect(jsonPath("$.campus[0].nombre").value("Obregón Náinari"))
                .andExpect(jsonPath("$.unidadesMedida[3].nombre").value("Metro cúbico"))
                .andExpect(jsonPath("$.presentaciones[2].nombre").value("Galón"));
    }

    @Test
    void consultarConCategoriaInexistenteResponde400() throws Exception {
        mockMvc.perform(get("/api/materiales").param("categoria", "NO_EXISTE")).andExpect(status().isBadRequest());
    }

    private static Material material(String codigo, String nombre) {
        return Material.crear(
                new CodigoMaterial(codigo),
                nombre,
                Categoria.PINTURA,
                UnidadMedida.LITRO,
                Presentacion.CUBETA,
                "19 L",
                Campus.OBREGON_NAINARI,
                new BigDecimal("450.00"),
                5,
                50,
                null);
    }
}
