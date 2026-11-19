package mx.itson.potroalmacen.personal.application.port.in;

/**
 * Datos básicos de un empleado para otros módulos.
 *
 * <p>Se expone en lugar de {@link mx.itson.potroalmacen.personal.domain.Empleado} para que otros módulos no
 * dependan del dominio de Personal.
 *
 * @param id id del empleado.
 * @param nombreCompleto nombre y apellidos.
 * @param puesto puesto del empleado.
 * @param activo si el empleado está activo.
 */
public record EmpleadoResumen(Integer id, String nombreCompleto, String puesto, boolean activo) {}
