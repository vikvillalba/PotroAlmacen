package mx.itson.potroalmacen.auditoria.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import mx.itson.potroalmacen.auditoria.domain.RegistroAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.AccionAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.EventoAuditable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@SpringBootTest
@Testcontainers
@Transactional
class RegistroAuditoriaPersistenceAdapterTest {

    @Container
    @ServiceConnection
    static MySQLContainer mysqlContainer = new MySQLContainer("mysql:8.4");

    @Autowired
    private RegistroAuditoriaPersistenceAdapter persistenceAdapter;

    @Autowired
    private SpringDataRegistroAuditoriaRepository repository;

    @Test
    void debeGuardarElRegistroConTodosSusDatos() {
        Instant ocurridoEn = Instant.parse("2026-11-22T15:30:00.123456Z");
        RegistroAuditoria registro = RegistroAuditoria.desdeEvento(new EventoDePrueba(
                7L, "OBREGON_NAINARI", AccionAuditoria.ENTREGAR, "salidas", "Boleta", "BOL-1A2B3C4D", ocurridoEn));

        RegistroAuditoria guardado = persistenceAdapter.guardar(registro);

        assertThat(guardado.getId()).isNotNull();
        RegistroAuditoriaEntity fila = repository.findById(guardado.getId()).orElseThrow();
        assertThat(fila.getUsuarioId()).isEqualTo(7L);
        assertThat(fila.getCampus()).isEqualTo("OBREGON_NAINARI");
        assertThat(fila.getAccion()).isEqualTo(AccionAuditoria.ENTREGAR);
        assertThat(fila.getModulo()).isEqualTo("salidas");
        assertThat(fila.getEntidad()).isEqualTo("Boleta");
        assertThat(fila.getReferencia()).isEqualTo("BOL-1A2B3C4D");
        assertThat(fila.getOcurridoEn().truncatedTo(ChronoUnit.MICROS)).isEqualTo(ocurridoEn);
    }

    private record EventoDePrueba(
            Long usuarioId,
            String campus,
            AccionAuditoria accion,
            String modulo,
            String entidad,
            String referencia,
            Instant ocurridoEn)
            implements EventoAuditable {}
}
