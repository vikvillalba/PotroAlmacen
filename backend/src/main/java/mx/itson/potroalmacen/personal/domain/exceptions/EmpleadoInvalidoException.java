package mx.itson.potroalmacen.personal.domain.exceptions;

public class EmpleadoInvalidoException extends IllegalArgumentException {
    public EmpleadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
