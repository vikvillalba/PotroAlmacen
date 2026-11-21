package mx.itson.potroalmacen.materiales.application.port.in;

/**
 * Existencia de un material, con los datos que otros módulos necesitan para mostrarla o validarla.
 *
 * <p>Se expone en lugar de {@link mx.itson.potroalmacen.materiales.domain.Material} para que otros módulos no
 * dependan del dominio de Materiales.
 *
 * @param codigoMaterial código del material.
 * @param nombreMaterial nombre del material.
 * @param unidadMedida abreviatura de la unidad en la que se cuenta la existencia, por ejemplo {@code L} o {@code pza}.
 * @param existencia unidades disponibles en inventario.
 */
public record ExistenciaMaterial(String codigoMaterial, String nombreMaterial, String unidadMedida, int existencia) {}
