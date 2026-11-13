/**
 * Eventos de dominio que publica el módulo Materiales.
 *
 * <p>Es una interfaz pública (named interface de Spring Modulith): otros módulos pueden suscribirse a estos eventos
 * sin depender de las clases internas de Materiales. Por eso los eventos solo llevan tipos simples (String, Integer,
 * Instant), no enums ni entidades del dominio.
 *
 * <p>Para suscribirse desde otro módulo (por ejemplo, Auditoría):
 *
 * <pre>{@code
 * @Component
 * class MaterialesListener {
 *
 *     @ApplicationModuleListener
 *     void alRegistrarMaterial(MaterialRegistrado evento) {
 *         // ...
 *     }
 * }
 * }</pre>
 *
 * <p>{@code @ApplicationModuleListener} recibe el evento solo después de que la transacción que lo publicó hace commit,
 * y corre en su propia transacción: si el suscriptor falla, el registro del material no se revierte.
 */
@NamedInterface("eventos")
package mx.itson.potroalmacen.materiales.eventos;

import org.springframework.modulith.NamedInterface;
