package mx.itson.potroalmacen.materiales.application.port.in;

/**
 * Límite de entrega por empleado de un material: la cantidad máxima que un empleado puede recibir dentro de un
 * periodo. Se expone en lugar de {@link mx.itson.potroalmacen.materiales.domain.Material} para que otros módulos no
 * dependan del dominio de Materiales.
 *
 * @param codigoMaterial código del material.
 * @param nombreMaterial nombre del material, para los mensajes de rechazo.
 * @param cantidadMaxima unidades que un empleado puede recibir en el periodo; mayor a cero.
 * @param periodoDias días hacia atrás, contados desde el momento de la entrega, en los que se suma lo recibido.
 */
public record LimiteEntrega(String codigoMaterial, String nombreMaterial, int cantidadMaxima, int periodoDias) {}
