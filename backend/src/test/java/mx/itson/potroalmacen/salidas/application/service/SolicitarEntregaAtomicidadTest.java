package mx.itson.potroalmacen.salidas.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
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
import mx.itson.potroalmacen.salidas.domain.exceptions.EmpleadoInactivoException;
import mx.itson.potroalmacen.salidas.domain.exceptions.LimiteEntregaExcedidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pruebas de integración de atomicidad para {@link SolicitarEntregaUseCase}.
 * Se ejecutan contra MySQL en Testcontainers con transacciones reales (NOT_SUPPORTED a nivel de test).
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class SolicitarEntregaAtomicidadTest {

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
        jdbcTemplate.execute("DELETE FROM mat_material WHERE codigo LIKE 'ATOM-%'");
    }

    @Test
    @DisplayName("Falla de existencia en el segundo material revierte la salida del primer material y no guarda boleta")
    void solicitar_fallaExistenciaEnSegundoMaterial_revierteSalidaDelPrimerMaterialYNoGuardaBoleta() {
        // Arrange:
        // Material 1 tiene 10 unidades, Material 2 tiene 2 unidades
        crearMaterial("ATOM-001", "Material Atomico 1", 10, null, null);
        crearMaterial("ATOM-002", "Material Atomico 2", 2, null, null);

        // Se solicitan 5 unidades del Material 1 y 5 unidades del Material 2 (el 2.º fallará)
        var comando = new SolicitudEntregaCommand(
                1, // María López (activa)
                2, // José Ramírez (activo)
                List.of(new MaterialSolicitado("ATOM-001", 5), new MaterialSolicitado("ATOM-002", 5)));

        // Act & Assert
        assertThrows(ExistenciaInsuficienteException.class, () -> solicitarEntregaUseCase.solicitar(comando));

        // Verificación de Atomicidad (Rollback integral):
        // El Material 1 NO debe quedar con 5; debe permanecer en sus 10 originales.
        MaterialEntity mat1 = materialRepository.findByCodigo("ATOM-001").orElseThrow();
        assertThat(mat1.getCantidad())
                .as("La existencia del primer material debió revertirse a 10 por la falla en el segundo")
                .isEqualTo(10);

        MaterialEntity mat2 = materialRepository.findByCodigo("ATOM-002").orElseThrow();
        assertThat(mat2.getCantidad())
                .as("La existencia del segundo material debió permanecer en 2")
                .isEqualTo(2);

        // No debe haberse guardado ninguna boleta ni detalle
        assertThat(boletaRepository.count())
                .as("No debe haberse guardado ninguna boleta")
                .isZero();
        Integer detallesGuardados =
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sal_boleta_detalle", Integer.class);
        assertThat(detallesGuardados)
                .as("No deben existir detalles de boleta huérfanos")
                .isZero();
    }

    @Test
    @DisplayName("Falla por límite de entrega excedido no altera existencias ni genera boleta")
    void solicitar_errorEnValidacionDeEmpleadoLimiteExcedido_noModificaExistencias() {
        // Arrange:
        // Material con límite de 2 unidades por empleado en periodo de 7 días, existencia inicial de 10
        crearMaterial("ATOM-003", "Material con Limite", 10, 2, 7);

        // Solicitante 1 pide 4 unidades (supera el límite de 2)
        var comando = new SolicitudEntregaCommand(1, 2, List.of(new MaterialSolicitado("ATOM-003", 4)));

        // Act & Assert
        assertThrows(LimiteEntregaExcedidoException.class, () -> solicitarEntregaUseCase.solicitar(comando));

        // Verificación: existencia intacta y sin boleta
        MaterialEntity mat = materialRepository.findByCodigo("ATOM-003").orElseThrow();
        assertThat(mat.getCantidad()).isEqualTo(10);
        assertThat(boletaRepository.count()).isZero();
    }

    @Test
    @DisplayName("Solicitante inactivo aborta la operación antes de tocar el inventario")
    void solicitar_solicitanteInactivo_noModificaInventario() {
        // Arrange:
        crearMaterial("ATOM-004", "Material Prueba Inactivo", 15, null, null);

        // Solicitante 7 es Rosa Valdez (inactiva según V202611192300__datos_mock_personal.sql)
        var comando = new SolicitudEntregaCommand(7, 2, List.of(new MaterialSolicitado("ATOM-004", 3)));

        // Act & Assert
        assertThrows(EmpleadoInactivoException.class, () -> solicitarEntregaUseCase.solicitar(comando));

        // Verificación: existencia intacta y sin boleta
        MaterialEntity mat = materialRepository.findByCodigo("ATOM-004").orElseThrow();
        assertThat(mat.getCantidad()).isEqualTo(15);
        assertThat(boletaRepository.count()).isZero();
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
        entity.setPrecioUnitario(new BigDecimal("15.00"));
        entity.setLimiteEntregaEmpleado(limiteEmpleado);
        entity.setPeriodoLimiteDias(periodoDias);
        materialRepository.saveAndFlush(entity);
    }
}
