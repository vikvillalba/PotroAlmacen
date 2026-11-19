package mx.itson.potroalmacen.salidas.domain.exceptions;

/**
 * El empleado indicado en la solicitud está inactivo y no puede participar en una entrega.
 *
 * <p>Extiende {@link IllegalArgumentException} para que el manejador global responda 400 (VALIDACION).
 */
public class EmpleadoInactivoException extends IllegalArgumentException {
    public static final String CODIGO = "EMPLEADO_INACTIVO";

    public EmpleadoInactivoException(Integer idEmpleado) {
        super("El empleado con id " + idEmpleado + " está inactivo.");
    }
}
