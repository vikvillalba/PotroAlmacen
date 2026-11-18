package mx.itson.potroalmacen.auditoria.domain.exceptions;

public class AuditoriaInvalidaException extends IllegalArgumentException {
    public static final String CODIGO = "AUDITORIA_INVALIDA";

    public AuditoriaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
