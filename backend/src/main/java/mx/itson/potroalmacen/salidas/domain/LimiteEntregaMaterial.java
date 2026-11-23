package mx.itson.potroalmacen.salidas.domain;

/**
 * Límite de entrega por empleado de un material, tal como lo necesita Salidas para validar una solicitud.
 *
 * @param cantidadMaxima unidades que un empleado puede recibir dentro del periodo.
 * @param periodoDias días hacia atrás, contados desde ahora, en los que se suma lo que ya recibió.
 */
public record LimiteEntregaMaterial(int cantidadMaxima, int periodoDias) {}
