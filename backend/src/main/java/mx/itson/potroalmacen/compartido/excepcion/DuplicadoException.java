package mx.itson.potroalmacen.compartido.excepcion;

/** Ya existe un recurso con la misma clave única. Se responde con 409. */
public class DuplicadoException extends NegocioException {

    public static final String CODIGO = "DUPLICADO";

    public DuplicadoException(String mensaje) {
        this(CODIGO, mensaje);
    }

    public DuplicadoException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}
