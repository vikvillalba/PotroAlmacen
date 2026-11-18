package mx.itson.potroalmacen.salidas.domain.exceptions;

public class BoletaInvalidaException extends IllegalArgumentException {
    public static final String CODIGO = "BOLETA_INVALIDA";

    public BoletaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
