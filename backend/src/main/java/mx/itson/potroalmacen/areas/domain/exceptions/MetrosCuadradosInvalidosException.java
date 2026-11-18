package mx.itson.potroalmacen.areas.domain.exceptions;

import mx.itson.potroalmacen.compartido.excepcion.NegocioException;

public class MetrosCuadradosInvalidosException extends NegocioException {

    public static final String CODIGO = "METROS_CUADRADOS_INVALIDOS";

    public MetrosCuadradosInvalidosException() {
        super(CODIGO, "Los metros cuadrados del área deben ser mayores a cero.");
    }
}