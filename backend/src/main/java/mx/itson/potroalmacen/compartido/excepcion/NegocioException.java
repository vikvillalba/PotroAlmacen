package mx.itson.potroalmacen.compartido.excepcion;

import java.util.Objects;

/**
 * Base de las excepciones de negocio que se traducen a una respuesta HTTP.
 *
 * <p>Es Java puro para que el dominio pueda lanzarlas sin depender de Spring. El constructor es de paquete: los
 * módulos extienden alguna de las subclases ({@link NoEncontradoException}, {@link DuplicadoException}, {@link
 * PermisoDenegadoException}), que es lo que decide el código HTTP.
 */
public abstract class NegocioException extends RuntimeException {

    private final String codigo;

    NegocioException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = Objects.requireNonNull(codigo, "El código de error es obligatorio");
    }

    /**
     * Código del error que se envía en la respuesta, por ejemplo {@code MATERIAL_DUPLICADO}.
     *
     * @return el código del error
     */
    public String getCodigo() {
        return codigo;
    }
}
