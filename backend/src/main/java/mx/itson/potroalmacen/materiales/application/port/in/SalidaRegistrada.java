package mx.itson.potroalmacen.materiales.application.port.in;

/**
 * Resultado de registrar la salida de un material del inventario.
 *
 * <p>Se expone en lugar de {@link mx.itson.potroalmacen.materiales.domain.Material} para que otros módulos no
 * dependan del dominio de Materiales.
 *
 * @param codigoMaterial código del material.
 * @param nombreMaterial nombre del material al momento de la salida.
 * @param cantidad unidades que salieron.
 */
public record SalidaRegistrada(String codigoMaterial, String nombreMaterial, int cantidad) {}
