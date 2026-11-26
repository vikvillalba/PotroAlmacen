package mx.itson.potroalmacen.auditoria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import mx.itson.potroalmacen.TestcontainersConfiguration;
import mx.itson.potroalmacen.materiales.adapter.out.persistence.MaterialEntity;
import mx.itson.potroalmacen.materiales.adapter.out.persistence.SpringDataMaterialRepository;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;
import mx.itson.potroalmacen.materiales.domain.exceptions.ExistenciaInsuficienteException;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitarEntregaUseCase;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand.MaterialSolicitado;
import mx.itson.potroalmacen.salidas.domain.Boleta;
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
 * Pruebas de integración entre Salidas y Auditoría: cada boleta generada deja su registro en la bitácora.
 *
 * <p>Se ejecutan contra MySQL en Testcontainers con transacciones reales (NOT_SUPPORTED a nivel de test), porque el
 * listener de Auditoría solo corre después del commit de la transacción que publicó {@code BoletaGenerada}.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class AuditoriaBoletaIntegracionTest {

    /** María López (activa) según V202611192300__datos_mock_personal.sql. */
    private static final int SOLICITANTE = 1;

    /** José Ramírez (activo) según V202611192300__datos_mock_personal.sql. */
    private static final int ENTREGADOR = 2;

    @Autowired
    private SolicitarEntregaUseCase solicitarEntregaUseCase;

    @Autowired
    private SpringDataMaterialRepository materialRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limpiarBaseDeDatos() {
        jdbcTemplate.execute("DELETE FROM aud_registro_auditoria");
        jdbcTemplate.execute("DELETE FROM sal_boleta_detalle");
        jdbcTemplate.execute("DELETE FROM sal_boleta");
        jdbcTemplate.execute("DELETE FROM mat_material WHERE codigo LIKE 'AUD-%'");
    }

    @Test
    @DisplayName("Una boleta generada deja un registro ENTREGAR del entregador con el folio como referencia")
    void solicitar_boletaGenerada_dejaRegistroDeAuditoria() {
        crearMaterial("AUD-001", 10);

        Boleta boleta = solicitarEntregaUseCase.solicitar(
                new SolicitudEntregaCommand(SOLICITANTE, ENTREGADOR, List.of(new MaterialSolicitado("AUD-001", 2))));

        List<Map<String, Object>> registros = jdbcTemplate.queryForList(
                "SELECT usuario_id, accion, modulo, entidad, referencia, ocurrido_en FROM aud_registro_auditoria");
        assertThat(registros)
                .as("Debe existir exactamente un registro de auditoría")
                .hasSize(1);

        Map<String, Object> registro = registros.get(0);
        assertThat(((Number) registro.get("usuario_id")).longValue())
                .as("El usuario auditado es el entregador")
                .isEqualTo(ENTREGADOR);
        assertThat(registro.get("accion")).isEqualTo("ENTREGAR");
        assertThat(registro.get("modulo")).isEqualTo("salidas");
        assertThat(registro.get("entidad")).isEqualTo("Boleta");
        assertThat(registro.get("referencia"))
                .as("La referencia es el folio de la boleta")
                .isEqualTo(boleta.getFolio());
        assertThat(registro.get("ocurrido_en")).isNotNull();
    }

    @Test
    @DisplayName("Cada boleta deja su propio registro: N boletas producen N registros con sus folios")
    void solicitar_variasBoletas_cadaUnaDejaSuRegistro() {
        crearMaterial("AUD-002", 20);

        List<String> folios = IntStream.range(0, 3)
                .mapToObj(i -> solicitarEntregaUseCase
                        .solicitar(new SolicitudEntregaCommand(
                                SOLICITANTE, ENTREGADOR, List.of(new MaterialSolicitado("AUD-002", 1))))
                        .getFolio())
                .toList();

        List<String> referencias = jdbcTemplate.queryForList(
                "SELECT referencia FROM aud_registro_auditoria WHERE modulo = 'salidas' AND entidad = 'Boleta'",
                String.class);
        assertThat(referencias)
                .as("Debe haber un registro por boleta, sin duplicados ni faltantes")
                .containsExactlyInAnyOrderElementsOf(folios);
    }

    @Test
    @DisplayName("Una solicitud que falla y se revierte no deja registro en la bitácora")
    void solicitar_fallida_noDejaRegistroDeAuditoria() {
        crearMaterial("AUD-003", 1);

        var comando =
                new SolicitudEntregaCommand(SOLICITANTE, ENTREGADOR, List.of(new MaterialSolicitado("AUD-003", 5)));

        assertThrows(ExistenciaInsuficienteException.class, () -> solicitarEntregaUseCase.solicitar(comando));

        Integer registros = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM aud_registro_auditoria", Integer.class);
        assertThat(registros)
                .as("Sin boleta no debe haber registro de auditoría")
                .isZero();
    }

    private void crearMaterial(String codigo, int cantidad) {
        MaterialEntity entity = new MaterialEntity();
        entity.setCodigo(codigo);
        entity.setNombre("Material Auditoría " + codigo);
        entity.setCategoria(Categoria.values()[0]);
        entity.setUnidadMedida(UnidadMedida.values()[0]);
        entity.setPresentacion(Presentacion.values()[0]);
        entity.setCampus(Campus.values()[0]);
        entity.setCantidad(cantidad);
        entity.setStockMinimo(0);
        entity.setPrecioUnitario(new BigDecimal("15.00"));
        materialRepository.saveAndFlush(entity);
    }
}
