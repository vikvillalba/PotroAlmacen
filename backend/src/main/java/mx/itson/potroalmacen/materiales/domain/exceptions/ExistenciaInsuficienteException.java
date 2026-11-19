package mx.itson.potroalmacen.materiales.domain.exceptions;

/**
 * La cantidad de salida de un material supera su existencia en inventario.
 *
 * <p>Extiende {@link IllegalArgumentException} para que el manejador global responda 400 (VALIDACION).
 */
public class ExistenciaInsuficienteException extends IllegalArgumentException {
    public static final String CODIGO = "EXISTENCIA_INSUFICIENTE";

    public ExistenciaInsuficienteException(String codigoMaterial, int existencia, int cantidadSolicitada) {
        super("Existencia insuficiente del material " + codigoMaterial + ": se solicitan " + cantidadSolicitada
                + " y hay " + existencia + " en inventario.");
    }
}
