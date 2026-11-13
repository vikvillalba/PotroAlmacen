package mx.itson.potroalmacen.materiales.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarMaterialCommand;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Departamento;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;
import mx.itson.potroalmacen.materiales.domain.exceptions.CodigoDuplicadoException;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException;
import mx.itson.potroalmacen.materiales.eventos.MaterialRegistrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegistrarMaterialServiceTest {

    private static final Instant AHORA = Instant.parse("2026-11-12T18:00:00Z");

    private FakeMaterialRepository repository;
    private FakePublicadorEventos publicador;
    private RegistrarMaterialService service;

    @BeforeEach
    void setUp() {
        repository = new FakeMaterialRepository();
        publicador = new FakePublicadorEventos();
        service = new RegistrarMaterialService(repository, publicador, Clock.fixed(AHORA, ZoneOffset.UTC));
    }

    @Nested
    @DisplayName("Casos de Prueba: Registros Exitosos")
    class CasosExitosos {

        @Test
        @DisplayName("VC-01: Registro de material exitoso con todos los campos")
        void vc01_registroMaterialExitosoConTodosLosCampos() {
            String precauciones = "No combinar con amoníaco, vinagre, alcohol etílico, limpiadores, ácido clorhídrico, "
                    + "ácido muriático ni agua oxigenada. No echar sobre la piel. No beber.";

            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    precauciones);

            Material registrado = service.registrar(comando);

            assertNotNull(registrado);
            assertNotNull(registrado.getId());
            assertEquals("MAT-001", registrado.getCodigo().toString());
            assertEquals("Lejía Clorox", registrado.getNombre());
            assertEquals(Categoria.SERVICIOS_GENERALES, registrado.getCategoria());
            assertEquals(Departamento.SERVICIOS_GENERALES, registrado.getDepartamento());
            assertEquals(UnidadMedida.LITRO, registrado.getUnidadMedida());
            assertEquals(Presentacion.BOTELLA, registrado.getPresentacion());
            assertEquals("1 L", registrado.getContenidoPresentacion());
            assertEquals(Campus.OBREGON_NAINARI, registrado.getCampus());
            assertEquals(new BigDecimal("22.50"), registrado.getPrecioUnitario());
            assertEquals(12, registrado.getCantidad());
            assertEquals(4, registrado.getStockMinimo());
            assertEquals(50, registrado.getStockMaximo());
            assertEquals(precauciones, registrado.getUsoPrecauciones());

            assertTrue(repository.existePorCodigo(new CodigoMaterial("MAT-001")));
        }

        @Test
        @DisplayName("VC-02: Registro de material exitoso sin stock máximo ni precauciones de uso")
        void vc02_registroExitosoSinStockMaximoNiPrecauciones() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    null,
                    null);

            Material registrado = service.registrar(comando);

            assertNotNull(registrado);
            assertNull(registrado.getStockMaximo());
            assertNull(registrado.getUsoPrecauciones());
            assertEquals(12, registrado.getCantidad());
            assertEquals(4, registrado.getStockMinimo());
        }

        @Test
        @DisplayName("VC-03: Registro de material exitoso con stock mínimo en cero")
        void vc03_registroExitosoConStockMinimoCero() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    0,
                    50,
                    "Uso con precaución");

            Material registrado = service.registrar(comando);

            assertNotNull(registrado);
            assertEquals(0, registrado.getStockMinimo());
            assertEquals(12, registrado.getCantidad());
            assertEquals(50, registrado.getStockMaximo());
        }

        @Test
        @DisplayName("Caso adicional: Registro exitoso con existencia inicial en cero (inferior al stock mínimo)")
        void registroExitosoConExistenciaInicialCero() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-002",
                    "Tornillo hexagonal 1/2 pulgada",
                    Categoria.FERRETERIA,
                    UnidadMedida.PIEZA,
                    Presentacion.CAJA,
                    "100 piezas",
                    Campus.OBREGON_CENTRO,
                    new BigDecimal("2.50"),
                    0,
                    20,
                    null,
                    null);

            Material registrado = service.registrar(comando);

            assertNotNull(registrado);
            assertEquals(0, registrado.getCantidad());
            assertEquals(20, registrado.getStockMinimo());
            assertTrue(registrado.estaBajoMinimo());
        }
    }

    @Nested
    @DisplayName("Casos de Prueba: Validaciones de Campos Obligatorios y Tipos")
    class CasosValidaciones {

        @Test
        @DisplayName("VC-04: Falla si el código está vacío o es nulo")
        void vc04_codigoVacioONulo_lanzaExcepcion() {
            RegistrarMaterialCommand comandoVacio = new RegistrarMaterialCommand(
                    "   ",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comandoVacio));

            RegistrarMaterialCommand comandoNulo = new RegistrarMaterialCommand(
                    null,
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comandoNulo));
        }

        @Test
        @DisplayName("VC-04: Falla si el nombre está vacío o es nulo")
        void vc04_nombreVacioONulo_lanzaExcepcion() {
            RegistrarMaterialCommand comandoVacio = new RegistrarMaterialCommand(
                    "MAT-001",
                    "   ",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comandoVacio));

            RegistrarMaterialCommand comandoNulo = new RegistrarMaterialCommand(
                    "MAT-001",
                    null,
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comandoNulo));
        }

        @Test
        @DisplayName("VC-04 y VC-08: Falla si la categoría es nula o inválida")
        void vc04_vc08_categoriaNula_lanzaExcepcion() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    null,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-04 y VC-09: Falla si la unidad de medida es nula o inválida")
        void vc04_vc09_unidadMedidaNula_lanzaExcepcion() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    null,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-04 y VC-10: Falla si la presentación es nula o inválida")
        void vc04_vc10_presentacionNula_lanzaExcepcion() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    null,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-04 y VC-07: Falla si el campus es nulo o inválido")
        void vc04_vc07_campusNulo_lanzaExcepcion() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    null,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-05: Falla cuando el código excede 30 caracteres")
        void vc05_codigoExcedeLongitud_lanzaExcepcion() {
            String codigoLargo = "KJASHDKSAHDJHSDKJFHD-918391839892831"; // 37 caracteres

            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    codigoLargo,
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-05: Falla cuando el nombre excede 500 caracteres")
        void vc05_nombreExcedeLongitud_lanzaExcepcion() {
            String nombreLargo = "A".repeat(501);

            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    nombreLargo,
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-05: Falla cuando el contenido de la presentación excede 120 caracteres")
        void vc05_contenidoPresentacionExcedeLongitud_lanzaExcepcion() {
            String contenidoLargo = "La botella contiene agua purificada, libre de impurezas y lista para beber. "
                    + "Su contenido se mantiene fresco gracias a su envase hermético."; // 138 caracteres

            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    contenidoLargo,
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-05: Falla cuando las precauciones de uso exceden 2000 caracteres")
        void vc05_precaucionesExcedeLongitud_lanzaExcepcion() {
            String precaucionesLargas = "X".repeat(2001);

            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    precaucionesLargas);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-11: Falla si el precio unitario es negativo")
        void vc11_precioUnitarioNegativo_lanzaExcepcion() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("-22.50"),
                    12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-11: Falla si la existencia inicial es negativa")
        void vc11_existenciaInicialNegativa_lanzaExcepcion() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    -12,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-11: Falla si el stock mínimo es negativo")
        void vc11_stockMinimoNegativo_lanzaExcepcion() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    -4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-11: Falla si el stock máximo es negativo")
        void vc11_stockMaximoNegativo_lanzaExcepcion() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    0,
                    4,
                    -50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }

        @Test
        @DisplayName("VC-13: Falla si el stock mínimo es igual o superior al stock máximo")
        void vc13_stockMinimoIgualOSuperiorAlMaximo_lanzaExcepcion() {
            // Caso igual: 50 y 50
            RegistrarMaterialCommand comandoIgual = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    50,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comandoIgual));

            // Caso superior: 60 y 50
            RegistrarMaterialCommand comandoSuperior = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    60,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comandoSuperior));
        }

        @Test
        @DisplayName("VC-14: Falla si la existencia inicial es superior al stock máximo")
        void vc14_existenciaInicialSuperiorAlStockMaximo_lanzaExcepcion() {
            // Ejemplo del PDF: existencia 51 y stock máximo 50
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    51,
                    4,
                    50,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comando));
        }
    }

    @Nested
    @DisplayName("Reglas de Negocio y Eventos de Dominio")
    class ReglasNegocioYEventos {

        @Test
        @DisplayName("RN-01: Falla con CodigoDuplicadoException si el código ya existe")
        void rn01_codigoDuplicado_lanzaCodigoDuplicadoException() {
            RegistrarMaterialCommand comando1 = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Lejía Clorox",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("22.50"),
                    12,
                    4,
                    50,
                    null);
            service.registrar(comando1);

            RegistrarMaterialCommand comandoDuplicado = new RegistrarMaterialCommand(
                    "MAT-001",
                    "Otra Lejía",
                    Categoria.SERVICIOS_GENERALES,
                    UnidadMedida.LITRO,
                    Presentacion.BOTELLA,
                    "1 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("20.00"),
                    10,
                    2,
                    40,
                    null);

            CodigoDuplicadoException excepcion =
                    assertThrows(CodigoDuplicadoException.class, () -> service.registrar(comandoDuplicado));

            assertEquals("MAT-001", excepcion.getCodigoMaterial());
            assertEquals("MATERIAL_DUPLICADO", excepcion.getCodigo());
        }

        @Test
        @DisplayName("Debe publicar MaterialRegistrado con los datos del material guardado")
        void registrar_publicaMaterialRegistrado() {
            Material registrado = service.registrar(new RegistrarMaterialCommand(
                    "EVT-001",
                    "Pintura Vinílica Blanca",
                    Categoria.PINTURA,
                    UnidadMedida.LITRO,
                    Presentacion.CUBETA,
                    "19 L",
                    Campus.OBREGON_NAINARI,
                    new BigDecimal("450.00"),
                    15,
                    5,
                    50,
                    null));

            assertEquals(
                    List.of(new MaterialRegistrado(
                            registrado.getId(), "EVT-001", "Pintura Vinílica Blanca", "OBREGON_NAINARI", 15, AHORA)),
                    publicador.eventos());
        }

        @Test
        @DisplayName("No debe publicar el evento si el código está duplicado")
        void registrar_codigoDuplicado_noPublicaEvento() {
            RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                    "EVT-002",
                    "Material Original",
                    Categoria.FERRETERIA,
                    UnidadMedida.PIEZA,
                    Presentacion.PIEZA_INDIVIDUAL,
                    null,
                    Campus.GUAYMAS,
                    BigDecimal.TEN,
                    0,
                    0,
                    null,
                    null);
            service.registrar(comando);

            assertThrows(CodigoDuplicadoException.class, () -> service.registrar(comando));

            assertEquals(1, publicador.eventos().size());
        }

        @Test
        @DisplayName("No debe publicar el evento si el material es inválido")
        void registrar_materialInvalido_noPublicaEvento() {
            RegistrarMaterialCommand comandoSinNombre = new RegistrarMaterialCommand(
                    "EVT-003",
                    "   ",
                    Categoria.FERRETERIA,
                    UnidadMedida.PIEZA,
                    Presentacion.PIEZA_INDIVIDUAL,
                    null,
                    Campus.NAVOJOA_SUR,
                    BigDecimal.ONE,
                    0,
                    0,
                    null,
                    null);

            assertThrows(MaterialInvalidoException.class, () -> service.registrar(comandoSinNombre));

            assertTrue(publicador.eventos().isEmpty());
        }
    }
}
