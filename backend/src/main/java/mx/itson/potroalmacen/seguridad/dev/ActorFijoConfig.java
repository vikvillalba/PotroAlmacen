package mx.itson.potroalmacen.seguridad.dev;

import mx.itson.potroalmacen.seguridad.ActorResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Registra el actor fijo solo en el perfil dev.
 *
 * <p>En cualquier otro perfil este bean no existe, así que el actor fijo nunca puede llegar a producción.
 */
@Configuration(proxyBeanMethods = false)
@Profile("dev")
@EnableConfigurationProperties(ActorFijoProperties.class)
class ActorFijoConfig {

    @Bean
    ActorResolver actorFijoResolver(ActorFijoProperties properties) {
        return new ActorFijoResolver(properties.toActor());
    }
}
