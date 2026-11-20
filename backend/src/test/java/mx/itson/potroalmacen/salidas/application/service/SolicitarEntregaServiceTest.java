package mx.itson.potroalmacen.salidas.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.domain.exceptions.ExistenciaInsuficienteException;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand.MaterialSolicitado;
import mx.itson.potroalmacen.salidas.application.port.out.BoletaRepositoryPort;
import mx.itson.potroalmacen.salidas.application.port.out.EmpleadoConsultaPort;
import mx.itson.potroalmacen.salidas.application.port.out.InventarioPort;
import mx.itson.potroalmacen.salidas.domain.Boleta;
import mx.itson.potroalmacen.salidas.domain.EmpleadoRef;
import mx.itson.potroalmacen.salidas.domain.EstadoBoleta;
import mx.itson.potroalmacen.salidas.domain.MaterialRef;
import mx.itson.potroalmacen.salidas.domain.exceptions.BoletaInvalidaException;
import mx.itson.potroalmacen.salidas.domain.exceptions.EmpleadoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SolicitarEntregaServiceTest {

    private static final EmpleadoRef ANA = new EmpleadoRef(1, "Ana López", "Intendente");
    private static final EmpleadoRef LUIS = new EmpleadoRef(2, "Luis Pérez", "Almacenista");

    private FakeInventario inventario;
    private FakeBoletaRepository boletas;
    private SolicitarEntregaService service;

    @BeforeEach
    void setUp() {
        inventario = new FakeInventario();
        inventario.existencias.put("LIM-001", 10);
        inventario.existencias.put("LIM-002", 3);
        boletas = new FakeBoletaRepository();
        EmpleadoConsultaPort empleados =
                id -> Optional.ofNullable(Map.of(1, ANA, 2, LUIS).get(id));
        service = new SolicitarEntregaService(empleados, inventario, boletas);
    }

    @Test
    @DisplayName("Registra la salida de cada material y guarda la boleta completada")
    void solicitar_valida() {
        Boleta boleta = service.solicitar(new SolicitudEntregaCommand(
                1, 2, List.of(new MaterialSolicitado("LIM-001", 4), new MaterialSolicitado(" LIM-002 ", 3))));

        assertEquals(1, boleta.getId());
        assertEquals(ANA, boleta.getSolicitante());
        assertEquals(LUIS, boleta.getEntregador());
        assertEquals(EstadoBoleta.COMPLETADA, boleta.getEstado());
        assertEquals(
                List.of(
                        new MaterialRef("LIM-001", "Material LIM-001", 4),
                        new MaterialRef("LIM-002", "Material LIM-002", 3)),
                boleta.getMateriales());
        assertEquals(6, inventario.existencias.get("LIM-001"));
        assertEquals(0, inventario.existencias.get("LIM-002"));
        assertEquals(1, boletas.guardadas.size());
    }

    @Test
    @DisplayName("Un empleado inexistente lanza EmpleadoNoEncontradoException sin tocar el inventario")
    void solicitar_empleadoInexistente() {
        var comando = new SolicitudEntregaCommand(1, 99, List.of(new MaterialSolicitado("LIM-001", 1)));

        assertThrows(EmpleadoNoEncontradoException.class, () -> service.solicitar(comando));
        assertTrue(inventario.salidas.isEmpty());
        assertTrue(boletas.guardadas.isEmpty());
    }

    @Test
    @DisplayName("Un solicitante nulo lo rechaza el dominio")
    void solicitar_solicitanteNulo() {
        var comando = new SolicitudEntregaCommand(null, 2, List.of(new MaterialSolicitado("LIM-001", 1)));

        assertThrows(BoletaInvalidaException.class, () -> service.solicitar(comando));
        assertTrue(boletas.guardadas.isEmpty());
    }

    @Test
    @DisplayName("Una línea sin código o con cantidad no positiva se rechaza antes de tocar el inventario")
    void solicitar_lineaInvalida() {
        var sinCodigo = new SolicitudEntregaCommand(
                1, 2, List.of(new MaterialSolicitado("LIM-001", 1), new MaterialSolicitado("  ", 1)));
        var cantidadCero = new SolicitudEntregaCommand(
                1, 2, List.of(new MaterialSolicitado("LIM-001", 1), new MaterialSolicitado("LIM-002", 0)));
        var cantidadNula = new SolicitudEntregaCommand(1, 2, List.of(new MaterialSolicitado("LIM-001", null)));

        assertThrows(BoletaInvalidaException.class, () -> service.solicitar(sinCodigo));
        assertThrows(BoletaInvalidaException.class, () -> service.solicitar(cantidadCero));
        assertThrows(BoletaInvalidaException.class, () -> service.solicitar(cantidadNula));
        assertTrue(inventario.salidas.isEmpty());
    }

    @Test
    @DisplayName("La existencia insuficiente que lanza Materiales se propaga y no se guarda la boleta")
    void solicitar_existenciaInsuficiente() {
        var comando = new SolicitudEntregaCommand(1, 2, List.of(new MaterialSolicitado("LIM-002", 5)));

        assertThrows(ExistenciaInsuficienteException.class, () -> service.solicitar(comando));
        assertTrue(boletas.guardadas.isEmpty());
    }

    @Test
    @DisplayName("Sin materiales lo rechaza el dominio")
    void solicitar_sinMateriales() {
        assertThrows(
                BoletaInvalidaException.class, () -> service.solicitar(new SolicitudEntregaCommand(1, 2, List.of())));
        assertTrue(boletas.guardadas.isEmpty());
    }

    /** Inventario en memoria: imita a Materiales verificando y descontando la existencia. */
    private static final class FakeInventario implements InventarioPort {
        final Map<String, Integer> existencias = new HashMap<>();
        final List<String> salidas = new ArrayList<>();

        @Override
        public MaterialRef registrarSalida(String codigoMaterial, int cantidad) {
            int existencia = existencias.get(codigoMaterial);
            if (cantidad > existencia) {
                throw new ExistenciaInsuficienteException(codigoMaterial, existencia, cantidad);
            }
            existencias.put(codigoMaterial, existencia - cantidad);
            salidas.add(codigoMaterial);
            return new MaterialRef(codigoMaterial, "Material " + codigoMaterial, cantidad);
        }
    }

    private static final class FakeBoletaRepository implements BoletaRepositoryPort {
        final List<Boleta> guardadas = new ArrayList<>();

        @Override
        public Boleta guardar(Boleta boleta) {
            Boleta conId = boleta.conIdYFolio(guardadas.size() + 1, boleta.getFolio());
            guardadas.add(conId);
            return conId;
        }
    }
}
