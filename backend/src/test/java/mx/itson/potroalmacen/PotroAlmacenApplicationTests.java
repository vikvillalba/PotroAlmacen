package mx.itson.potroalmacen;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/** La aplicación arranca contra MySQL, Flyway se conecta y Hibernate valida el esquema. */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class PotroAlmacenApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void flywayCreaLaTablaDeHistorial() {
        Integer tablas = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
                        + "WHERE table_schema = DATABASE() AND table_name = 'flyway_schema_history'",
                Integer.class);

        assertThat(tablas).isEqualTo(1);
    }
}
