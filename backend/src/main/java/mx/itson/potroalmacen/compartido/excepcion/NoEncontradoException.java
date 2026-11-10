package mx.itson.potroalmacen.compartido.excepcion;

/** El recurso solicitado no existe. Se responde con 404. */
public class NoEncontradoException extends NegocioException {

    public static final String CODIGO = "NO_ENCONTRADO";

    public NoEncontradoException(String mensaje) {
        this(CODIGO, mensaje);
    }

    public NoEncontradoException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}
