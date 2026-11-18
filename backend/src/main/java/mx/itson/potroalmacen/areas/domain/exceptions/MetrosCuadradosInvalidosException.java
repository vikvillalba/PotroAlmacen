package mx.itson.potroalmacen.areas.domain.exceptions;

public class MetrosCuadradosInvalidosException extends IllegalArgumentException {

    public static final String CODIGO = "METROS_CUADRADOS_INVALIDOS";

    public MetrosCuadradosInvalidosException() {
        super("Los metros cuadrados del área deben ser mayores a cero.");
    }
}
