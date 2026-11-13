# ADR 0001: Hilos en los que corren los suscriptores de eventos

- **Estado:** Pendiente
- **Fecha:** 2026-11-12
- **Relacionado con:** publicación del evento `MaterialRegistrado` (módulo Materiales)

## Contexto

El módulo Materiales publica eventos de dominio (por ahora `MaterialRegistrado`) con el patrón Observer:
`RegistrarMaterialService` → `PublicadorEventosPort` → `ApplicationEventPublisher` de Spring. Los demás módulos
se suscriben con `@ApplicationModuleListener`.

`@ApplicationModuleListener` combina tres cosas:

- `@TransactionalEventListener`: el suscriptor recibe el evento **después del commit** del registro.
- `@Transactional(propagation = REQUIRES_NEW)`: el suscriptor corre en **su propia transacción**.
- `@Async`: el suscriptor corre en **otro hilo**, pero **solo si la aplicación tiene `@EnableAsync`**.

Hoy la aplicación **no** tiene `@EnableAsync`, así que `@Async` se ignora y los suscriptores corren en el mismo hilo
de la petición HTTP, justo después del commit. Todavía no hay suscriptores, así que esto no tiene efecto aún.

## Problema

Cuando existan suscriptores (por ejemplo, la bitácora de Auditoría o alertas de stock), hay que decidir en qué hilo
corren:

- **Mismo hilo (situación actual):** la respuesta de la API espera a que terminen todos los suscriptores. Un
  suscriptor lento hace lenta la petición. Es más fácil de depurar y de probar.
- **Otro hilo (`@EnableAsync`):** la API responde sin esperar. Hay que configurar el pool de hilos (tamaño, cola,
  qué pasa al apagar la aplicación) y las pruebas deben esperar a que el suscriptor termine.

En ambos casos, si un suscriptor falla el material ya quedó registrado y **el evento se pierde**, porque no hay
registro de publicaciones de eventos.

## Opciones

1. **Dejarlo síncrono.** No se cambia nada. Válido mientras los suscriptores sean rápidos.
2. **Activar `@EnableAsync` con un pool configurado.** Se agrega `@EnableAsync` en `compartido.config` y se define el
   pool con `spring.task.execution.*` (tamaño, cola, `shutdown.await-termination`).
3. **Opción 2 más el registro de publicaciones de eventos.** Se agrega `spring-modulith-starter-jpa`: cada evento se
   guarda en una tabla (con su migración de Flyway) y se marca como completado cuando el suscriptor termina. Los
   eventos pendientes se pueden reintentar, por ejemplo al reiniciar la aplicación.

## Decisión

**Pendiente.** Se decide cuando se agregue el primer suscriptor.

## Consecuencias

Por definir según la opción elegida. Mientras tanto:

- Los suscriptores corren en el hilo de la petición, después del commit.
- Un suscriptor que falle no revierte el registro del material, pero el evento no se reintenta.
