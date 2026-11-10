/**
 * Infraestructura compartida por todos los módulos (no es un módulo de negocio).
 *
 * <ul>
 *   <li>{@code excepcion}: excepciones base de negocio, en Java puro, que los dominios pueden extender.
 *   <li>{@code web}: formato estándar de errores de la API y el {@code @RestControllerAdvice} base.
 *   <li>{@code config}: configuración de springdoc (Swagger).
 * </ul>
 *
 * <p>Se declara como módulo abierto para que cualquier módulo pueda usar sus clases sin que Spring Modulith lo marque
 * como violación de fronteras.
 */
@ApplicationModule(displayName = "Compartido", type = ApplicationModule.Type.OPEN)
package mx.itson.potroalmacen.compartido;

import org.springframework.modulith.ApplicationModule;
