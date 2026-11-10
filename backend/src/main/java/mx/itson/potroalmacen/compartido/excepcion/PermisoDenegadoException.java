package mx.itson.potroalmacen.compartido.excepcion;

/** El actor no tiene permiso para la operación. Se responde con 403. */
public class PermisoDenegadoException extends NegocioException {

    public static final String CODIGO = "PERMISO_DENEGADO";

    public PermisoDenegadoException(String mensaje) {
        this(CODIGO, mensaje);
    }

    public PermisoDenegadoException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}
