package mx.itson.potroalmacen;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * MySQL en un contenedor para las pruebas de integración.
 *
 * <p>Usa la misma imagen que docker compose. {@code @ServiceConnection} configura el datasource y Flyway con los datos
 * del contenedor, sin escribir URL ni contraseñas.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    MySQLContainer mysqlContainer() {
        return new MySQLContainer(DockerImageName.parse("mysql:8.4"));
    }
}
