package mx.itson.potroalmacen.salidas.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import mx.itson.potroalmacen.TestcontainersConfiguration;
import mx.itson.potroalmacen.materiales.adapter.out.persistence.MaterialEntity;
import mx.itson.potroalmacen.materiales.adapter.out.persistence.SpringDataMaterialRepository;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;
import mx.itson.potroalmacen.materiales.domain.exceptions.ExistenciaInsuficienteException;
import mx.itson.potroalmacen.salidas.adapter.out.persistence.SpringDataBoletaRepository;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitarEntregaUseCase;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand.MaterialSolicitado;
import mx.itson.potroalmacen.salidas.domain.exceptions.LimiteEntregaExcedidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pruebas de integración de concurrencia para {@link SolicitarEntregaUseCase}.
 * Evalúan condiciones de carrera, sobreventa y consistencia transaccional bajo acceso concurrente real en MySQL.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class SolicitarEntregaConcurrenciaTest {

    @Autowired
    private SolicitarEntregaUseCase solicitarEntregaUseCase;

    @Autowired
    private SpringDataMaterialRepository materialRepository;

    @Autowired
    private SpringDataBoletaRepository boletaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limpiarBaseDeDatos() {
        jdbcTemplate.execute("DELETE FROM sal_boleta_detalle");
        jdbcTemplate.execute("DELETE FROM sal_boleta");
        jdbcTemplate.execute("DELETE FROM mat_material WHERE codigo LIKE 'CONC-%'");
    }

    @Test
    @DisplayName("Concurrencia con stock limitado evita sobreventa y mantiene consistencia exacta")
    void solicitar_concurrenciaStockLimitado_evitaSobreventaYDescuentoInconsistente() throws InterruptedException {
        // Arrange:
        // Material con existencia inicial de 10 unidades
        String codigoMaterial = "CONC-001";
        crearMaterial(codigoMaterial, "Material Concurrente Stock", 10, null, null);

        int numeroHilos = 10;
        int cantidadPorSolicitud = 2; // 10 hilos * 2 = 20 demandadas, pero solo hay 10 disponibles
        AtomicInteger exitos;
        AtomicInteger fallosExistenciaOConcurrencia;
        AtomicInteger otrosFallos;
        boolean completado;
        try (ExecutorService executor = Executors.newFixedThreadPool(numeroHilos)) {
            CountDownLatch barreraInicio = new CountDownLatch(1);
            CountDownLatch barreraFin = new CountDownLatch(numeroHilos);

            exitos = new AtomicInteger(0);
            fallosExistenciaOConcurrencia = new AtomicInteger(0);
            otrosFallos = new AtomicInteger(0);

            // Act:
            // Lanzamos 10 hilos simultáneos
            for (int i = 0; i < numeroHilos; i++) {
                executor.submit(() -> {
                    try {
                        barreraInicio.await(); // Esperar disparo simultáneo
                        solicitarEntregaUseCase.solicitar(new SolicitudEntregaCommand(
                                1, // María López
                                2, // José Ramírez
                                List.of(new MaterialSolicitado(codigoMaterial, cantidadPorSolicitud))));
                        exitos.incrementAndGet();
                    } catch (ExistenciaInsuficienteException | ConcurrencyFailureException ex) {
                        fallosExistenciaOConcurrencia.incrementAndGet();
                    } catch (Exception ex) {
                        otrosFallos.incrementAndGet();
                    } finally {
                        barreraFin.countDown();
                    }
                });
            }

            // Disparo simultáneo de todos los hilos
            barreraInicio.countDown();
            completado = barreraFin.await(15, TimeUnit.SECONDS);
            executor.shutdown();
        }

        // Assert:
        assertThat(completado).as("Todos los hilos debieron terminar a tiempo").isTrue();
        assertThat(otrosFallos.get())
                .as("No debe haber excepciones inesperadas")
                .isZero();

        // En un sistema consistente y concurrente:
        // Solo 5 solicitudes deben haber tenido éxito (5 * 2 = 10)
        // Y 5 deben haber fallado por falta de existencia o conflicto de concurrencia
        assertThat(exitos.get())
                .as("Exactamente 5 solicitudes debieron completarse con éxito (10 disponibles / 2 solicitadas)")
                .isEqualTo(5);
        assertThat(fallosExistenciaOConcurrencia.get())
                .as("Exactamente 5 solicitudes debieron ser rechazadas")
                .isEqualTo(5);

        // La existencia en la base de datos debe ser exactamente 0 (sin sobreventa ni números negativos)
        MaterialEntity material =
                materialRepository.findByCodigo(codigoMaterial).orElseThrow();
        assertThat(material.getCantidad())
                .as("La existencia final debe ser exactamente 0")
                .isZero();

        // Verificamos que en la BD se hayan registrado exactamente 5 boletas
        long totalBoletas = boletaRepository.count();
        assertThat(totalBoletas)
                .as("Deben existir exactamente 5 boletas generadas")
                .isEqualTo(5);

        // Y la suma total de material entregado en los detalles debe ser exactamente 10
        Integer totalEntregadoEnDetalles = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(cantidad), 0) FROM sal_boleta_detalle WHERE codigo_material = ?",
                Integer.class,
                codigoMaterial);
        assertThat(totalEntregadoEnDetalles)
                .as("La suma de material entregado en los detalles debe ser exactamente 10")
                .isEqualTo(10);
    }

    @Test
    @DisplayName("Concurrencia con límite por empleado no permite superar el tope permitido")
    void solicitar_concurrenciaMismoEmpleadoLimitePeriodo_noPermiteSuperarTope() throws InterruptedException {
        // Arrange:
        // Material con stock suficiente (30), pero con límite de 5 unidades por empleado en 7 días
        String codigoMaterial = "CONC-002";
        crearMaterial(codigoMaterial, "Material Concurrente Limite Empleado", 30, 5, 7);

        int numeroHilos = 2;
        int cantidadPorSolicitud = 4; // 2 solicitudes * 4 = 8 unidades > límite de 5
        AtomicInteger exitos;
        AtomicInteger fallosLimite;
        boolean completado;
        try (ExecutorService executor = Executors.newFixedThreadPool(numeroHilos)) {
            CountDownLatch barreraInicio = new CountDownLatch(1);
            CountDownLatch barreraFin = new CountDownLatch(numeroHilos);

            exitos = new AtomicInteger(0);
            fallosLimite = new AtomicInteger(0);

            for (int i = 0; i < numeroHilos; i++) {
                executor.submit(() -> {
                    try {
                        barreraInicio.await();
                        solicitarEntregaUseCase.solicitar(new SolicitudEntregaCommand(
                                1, // Mismo solicitante
                                2,
                                List.of(new MaterialSolicitado(codigoMaterial, cantidadPorSolicitud))));
                        exitos.incrementAndGet();
                    } catch (LimiteEntregaExcedidoException | ConcurrencyFailureException ex) {
                        fallosLimite.incrementAndGet();
                    } catch (Exception ex) {
                        // Otros fallos
                    } finally {
                        barreraFin.countDown();
                    }
                });
            }

            barreraInicio.countDown();
            completado = barreraFin.await(10, TimeUnit.SECONDS);
            executor.shutdown();
        }

        assertThat(completado).isTrue();

        // Solo 1 solicitud de 4 unidades debe tener éxito. La segunda debe fallar porque 4 + 4 = 8 > 5
        assertThat(exitos.get())
                .as("Solo 1 solicitud debió permitirse para no exceder el límite de 5 unidades")
                .isEqualTo(1);
        assertThat(fallosLimite.get())
                .as("La segunda solicitud concurrente debió ser rechazada por exceder el límite")
                .isEqualTo(1);

        // El total entregado al empleado no puede exceder 5
        Integer totalEntregado = jdbcTemplate.queryForObject("""
                SELECT COALESCE(SUM(d.cantidad), 0)
                FROM sal_boleta b
                JOIN sal_boleta_detalle d ON d.boleta_id = b.id
                WHERE b.solicitante_id = 1 AND d.codigo_material = ?
                """, Integer.class, codigoMaterial);
        assertThat(totalEntregado)
                .as("El total de material entregado al empleado no debe rebasar el límite de 5")
                .isLessThanOrEqualTo(5);
    }

    private void crearMaterial(
            String codigo, String nombre, int cantidad, Integer limiteEmpleado, Integer periodoDias) {
        MaterialEntity entity = new MaterialEntity();
        entity.setCodigo(codigo);
        entity.setNombre(nombre);
        entity.setCategoria(Categoria.values()[0]);
        entity.setUnidadMedida(UnidadMedida.values()[0]);
        entity.setPresentacion(Presentacion.values()[0]);
        entity.setCampus(Campus.values()[0]);
        entity.setCantidad(cantidad);
        entity.setStockMinimo(0);
        entity.setPrecioUnitario(new BigDecimal("20.00"));
        entity.setLimiteEntregaEmpleado(limiteEmpleado);
        entity.setPeriodoLimiteDias(periodoDias);
        materialRepository.saveAndFlush(entity);
    }
}
