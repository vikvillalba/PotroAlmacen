package mx.itson.potroalmacen.salidas.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand.MaterialSolicitado;
import mx.itson.potroalmacen.salidas.application.port.in.ValidarEmpleadoMaterialCommand;
import mx.itson.potroalmacen.salidas.application.port.out.BoletaRepositoryPort;
import mx.itson.potroalmacen.salidas.application.port.out.EmpleadoConsultaPort;
import mx.itson.potroalmacen.salidas.application.port.out.LimiteEntregaPort;
import mx.itson.potroalmacen.salidas.domain.Boleta;
import mx.itson.potroalmacen.salidas.domain.EmpleadoRef;
import mx.itson.potroalmacen.salidas.domain.LimiteEntregaMaterial;
import mx.itson.potroalmacen.salidas.domain.exceptions.BoletaInvalidaException;
import mx.itson.potroalmacen.salidas.domain.exceptions.EmpleadoInactivoException;
import mx.itson.potroalmacen.salidas.domain.exceptions.EmpleadoNoEncontradoException;
import mx.itson.potroalmacen.salidas.domain.exceptions.LimiteEntregaExcedidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValidarEmpleadoMaterialServiceTest {

    private static final EmpleadoRef EMPLEADO_ACTIVO = new EmpleadoRef(1, "Carlos López", "Profesor");
    private static final ZoneId ZONA = ZoneId.of("America/Hermosillo");
    private static final Instant AHORA_INSTANTE = Instant.parse("2026-11-22T10:00:00Z");

    private FakeEmpleadoConsulta empleados;
    private FakeLimiteEntrega limites;
    private FakeBoletaRepository boletas;
    private ValidarEmpleadoMaterialService service;

    @BeforeEach
    void setUp() {
        empleados = new FakeEmpleadoConsulta();
        empleados.agregar();

        limites = new FakeLimiteEntrega();
        boletas = new FakeBoletaRepository();

        Clock fixedClock = Clock.fixed(AHORA_INSTANTE, ZONA);
        service = new ValidarEmpleadoMaterialService(empleados, limites, boletas, fixedClock);
    }

    @Test
    @DisplayName("Pasa la validación cuando los materiales solicitados no tienen límite configurado")
    void validar_materialesSinLimite_exitoso() {
        var comando = new ValidarEmpleadoMaterialCommand(
                1, List.of(new MaterialSolicitado("MAT-001", 5), new MaterialSolicitado("MAT-002", 10)));

        assertDoesNotThrow(() -> service.validarEmpleado(comando));
    }

    @Test
    @DisplayName("Pasa la validación cuando la cantidad solicitada más lo ya entregado no supera el límite")
    void validar_conLimite_dentroDelLimite_exitoso() {
        limites.configurar("MAT-PINCEL", new LimiteEntregaMaterial(5, 7));
        boletas.registrarEntregas("MAT-PINCEL", 2);

        var comando = new ValidarEmpleadoMaterialCommand(
                1, List.of(new MaterialSolicitado("MAT-PINCEL", 3))); // 2 + 3 = 5 <= 5

        assertDoesNotThrow(() -> service.validarEmpleado(comando));
    }

    @Test
    @DisplayName("Lanza LimiteEntregaExcedidoException cuando la solicitud supera el límite de entrega")
    void validar_conLimite_superaLimite_lanzaExcepcion() {
        limites.configurar("MAT-PINCEL", new LimiteEntregaMaterial(5, 7));
        boletas.registrarEntregas("MAT-PINCEL", 3);

        var comando = new ValidarEmpleadoMaterialCommand(
                1, List.of(new MaterialSolicitado("MAT-PINCEL", 3))); // 3 + 3 = 6 > 5

        LimiteEntregaExcedidoException ex =
                assertThrows(LimiteEntregaExcedidoException.class, () -> service.validarEmpleado(comando));

        assertEquals("MAT-PINCEL", ex.getCodigoMaterial());
        assertEquals(5, ex.getLimiteMaximo());
        assertEquals(7, ex.getPeriodoDias());
        assertEquals(3, ex.getCantidadEntregada());
        assertEquals(3, ex.getCantidadSolicitada());
        assertTrue(ex.getMessage().contains("MAT-PINCEL"));
    }

    @Test
    @DisplayName("Agrupa las cantidades del mismo material repetido en varias líneas y valida el total")
    void validar_materialRepetido_acumulaCantidadYRechazaSiExcede() {
        limites.configurar("MAT-HOJAS", new LimiteEntregaMaterial(10, 15));
        boletas.registrarEntregas("MAT-HOJAS", 4);

        // Se solicitan 4 + 3 = 7 unidades. Total con historial: 4 + 7 = 11 > 10
        var comando = new ValidarEmpleadoMaterialCommand(
                1, List.of(new MaterialSolicitado("MAT-HOJAS", 4), new MaterialSolicitado("MAT-HOJAS", 3)));

        LimiteEntregaExcedidoException ex =
                assertThrows(LimiteEntregaExcedidoException.class, () -> service.validarEmpleado(comando));

        assertEquals("MAT-HOJAS", ex.getCodigoMaterial());
        assertEquals(10, ex.getLimiteMaximo());
        assertEquals(4, ex.getCantidadEntregada());
        assertEquals(7, ex.getCantidadSolicitada());
    }

    @Test
    @DisplayName("Lanza EmpleadoNoEncontradoException cuando el solicitante no existe")
    void validar_empleadoNoExiste_lanzaExcepcion() {
        var comando = new ValidarEmpleadoMaterialCommand(999, List.of(new MaterialSolicitado("MAT-001", 1)));

        assertThrows(EmpleadoNoEncontradoException.class, () -> service.validarEmpleado(comando));
    }

    @Test
    @DisplayName("Lanza EmpleadoInactivoException cuando el solicitante está inactivo")
    void validar_empleadoInactivo_lanzaExcepcion() {
        empleados.marcarInactivo();

        var comando = new ValidarEmpleadoMaterialCommand(2, List.of(new MaterialSolicitado("MAT-001", 1)));

        assertThrows(EmpleadoInactivoException.class, () -> service.validarEmpleado(comando));
    }

    @Test
    @DisplayName("Lanza BoletaInvalidaException ante solicitudes mal formadas o líneas inválidas")
    void validar_solicitudesInvalidas() {
        assertThrows(BoletaInvalidaException.class, () -> service.validarEmpleado(null));

        var sinSolicitante = new ValidarEmpleadoMaterialCommand(null, List.of(new MaterialSolicitado("MAT-001", 1)));
        assertThrows(BoletaInvalidaException.class, () -> service.validarEmpleado(sinSolicitante));

        var sinMateriales = new ValidarEmpleadoMaterialCommand(1, List.of());
        assertThrows(BoletaInvalidaException.class, () -> service.validarEmpleado(sinMateriales));

        var codigoVacio = new ValidarEmpleadoMaterialCommand(1, List.of(new MaterialSolicitado("  ", 2)));
        assertThrows(BoletaInvalidaException.class, () -> service.validarEmpleado(codigoVacio));

        var cantidadCero = new ValidarEmpleadoMaterialCommand(1, List.of(new MaterialSolicitado("MAT-001", 0)));
        assertThrows(BoletaInvalidaException.class, () -> service.validarEmpleado(cantidadCero));

        var cantidadNegativa = new ValidarEmpleadoMaterialCommand(1, List.of(new MaterialSolicitado("MAT-001", -2)));
        assertThrows(BoletaInvalidaException.class, () -> service.validarEmpleado(cantidadNegativa));
    }

    @Test
    @DisplayName("Permite usar fechaReferencia personalizada en el comando para el cálculo de periodo")
    void validar_conFechaReferenciaEnComando() {
        limites.configurar("MAT-TIJERAS", new LimiteEntregaMaterial(2, 30));

        LocalDateTime fechaEspecifica = LocalDateTime.of(2026, 11, 20, 12, 0);
        boletas.registrarEntregas("MAT-TIJERAS", 2);

        var comando = new ValidarEmpleadoMaterialCommand(
                1, List.of(new MaterialSolicitado("MAT-TIJERAS", 1)), fechaEspecifica);

        assertThrows(LimiteEntregaExcedidoException.class, () -> service.validarEmpleado(comando));
        assertEquals(fechaEspecifica.minusDays(30), boletas.ultimaFechaDesdeConsultada);
    }

    // Fakes para pruebas unitarias sin levantar Spring ni base de datos

    private static final class FakeEmpleadoConsulta implements EmpleadoConsultaPort {
        private final Map<Integer, EmpleadoRef> activos = new HashMap<>();
        private final Map<Integer, Boolean> inactivos = new HashMap<>();

        void agregar() {
            activos.put(
                    ValidarEmpleadoMaterialServiceTest.EMPLEADO_ACTIVO.idEmpleado(),
                    ValidarEmpleadoMaterialServiceTest.EMPLEADO_ACTIVO);
        }

        void marcarInactivo() {
            inactivos.put(2, true);
        }

        @Override
        public Optional<EmpleadoRef> buscarPorId(Integer idEmpleado) {
            if (inactivos.containsKey(idEmpleado)) {
                throw new EmpleadoInactivoException(idEmpleado);
            }
            return Optional.ofNullable(activos.get(idEmpleado));
        }
    }

    private static final class FakeLimiteEntrega implements LimiteEntregaPort {
        private final Map<String, LimiteEntregaMaterial> limites = new HashMap<>();

        void configurar(String codigo, LimiteEntregaMaterial limite) {
            limites.put(codigo, limite);
        }

        @Override
        public Optional<LimiteEntregaMaterial> buscarLimite(String codigoMaterial) {
            return Optional.ofNullable(limites.get(codigoMaterial));
        }
    }

    private static final class FakeBoletaRepository implements BoletaRepositoryPort {
        private final Map<String, Integer> entregas = new HashMap<>();
        LocalDateTime ultimaFechaDesdeConsultada;

        void registrarEntregas(String codigoMaterial, int cantidad) {
            entregas.put(1 + ":" + codigoMaterial, cantidad);
        }

        @Override
        public Boleta guardar(Boleta boleta) {
            return boleta;
        }

        @Override
        public List<Boleta> listarTodas() {
            return List.of();
        }

        @Override
        public Optional<Boleta> buscarPorId(Integer id) {
            return Optional.empty();
        }

        @Override
        public int cantidadEntregada(Integer solicitanteId, String codigoMaterial, LocalDateTime desde) {
            this.ultimaFechaDesdeConsultada = desde;
            return entregas.getOrDefault(solicitanteId + ":" + codigoMaterial, 0);
        }
    }
}
