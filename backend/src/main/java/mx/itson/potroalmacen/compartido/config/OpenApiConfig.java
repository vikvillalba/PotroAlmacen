package mx.itson.potroalmacen.compartido.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Información general de la documentación de la API.
 *
 * <p>Swagger UI solo se publica en el perfil dev (ver application-dev.yml): {@code /swagger-ui/index.html}.
 */
@Configuration(proxyBeanMethods = false)
class OpenApiConfig {

    @Bean
    OpenAPI potroAlmacenOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Potro Almacén API")
                        .version("v1")
                        .description("API del sistema de almacenes de Servicios Generales y Mantenimiento de ITSON."));
    }
}
