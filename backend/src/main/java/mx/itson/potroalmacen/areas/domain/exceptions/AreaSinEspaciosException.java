package mx.itson.potroalmacen.areas.domain.exceptions;

public class AreaSinEspaciosException extends IllegalArgumentException {

    public static final String CODIGO = "AREA_SIN_ESPACIOS";

    public AreaSinEspaciosException() {
        super("Un área debe contener al menos un espacio registrado.");
    }
}
