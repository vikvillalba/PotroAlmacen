package mx.itson.potroalmacen.materiales.adapter.in.web;

/**
 * Valor de un catálogo para el frontend.
 *
 * @param id nombre del enum; es lo que se envía de vuelta en filtros y registros (por ejemplo {@code PINTURA})
 * @param nombre etiqueta que se muestra en pantalla (por ejemplo {@code Pintura})
 */
public record OpcionCatalogo(String id, String nombre) {

    public static OpcionCatalogo de(Enum<?> valor, String etiqueta) {
        return new OpcionCatalogo(valor.name(), etiqueta);
    }
}
