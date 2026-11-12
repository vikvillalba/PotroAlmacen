package mx.itson.potroalmacen.materiales.domain.exceptions;

/**
 * Un dato del material no cumple las reglas del dominio.
 *
 * <p>Extiende {@link IllegalArgumentException} para que el manejador global responda 400 (VALIDACION).
 */
public class MaterialInvalidoException extends IllegalArgumentException {
    public MaterialInvalidoException(String mensaje) {
        super(mensaje);
    }
}
