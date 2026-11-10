/**
 * Seguridad (configuración transversal, sin hexágono).
 *
 * <p>Autenticación, autorización por endpoint y resolución del usuario actual. Su API pública es {@link
 * mx.itson.potroalmacen.seguridad.Actor} y {@link mx.itson.potroalmacen.seguridad.ActorResolver}: los adaptadores de
 * entrada de cada módulo obtienen el Actor y lo pasan en los comandos de los casos de uso.
 */
@ApplicationModule(displayName = "Seguridad")
package mx.itson.potroalmacen.seguridad;

import org.springframework.modulith.ApplicationModule;
