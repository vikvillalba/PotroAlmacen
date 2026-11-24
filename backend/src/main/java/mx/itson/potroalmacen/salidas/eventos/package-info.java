/**
 * Eventos de dominio que publica el módulo Salidas.
 *
 * <p>Es una interfaz pública (named interface de Spring Modulith): otros módulos pueden suscribirse a estos eventos
 * sin depender de las clases internas de Salidas. Por eso los eventos solo llevan tipos simples (String, Integer,
 * Instant), no entidades del dominio.
 */
@NamedInterface("eventos")
package mx.itson.potroalmacen.salidas.eventos;

import org.springframework.modulith.NamedInterface;
