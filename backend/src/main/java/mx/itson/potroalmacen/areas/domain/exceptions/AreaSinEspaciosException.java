package mx.itson.potroalmacen.areas.domain.exceptions;

public class AreaSinEspaciosException extends NegocioException {

    public static final String CODIGO = "AREA_SIN_ESPACIOS";

    public AreaSinEspaciosException() {
        super(CODIGO, "Un área debe contener al menos un espacio registrado.");
    }
}