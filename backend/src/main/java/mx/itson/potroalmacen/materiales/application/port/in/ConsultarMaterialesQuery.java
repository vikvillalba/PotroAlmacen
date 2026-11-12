package mx.itson.potroalmacen.materiales.application.port.in;

import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.Presentacion;

/**
 * Criterios que llegan de la API para consultar materiales. Todos son opcionales: un campo nulo (o texto en blanco)
 * no filtra, y los que se den se combinan con Y.
 *
 * @param nombre parte del nombre, sin distinguir mayúsculas
 * @param codigo parte del código, sin distinguir mayúsculas
 * @param categoria categoría exacta
 * @param presentacion presentación exacta
 */
public record ConsultarMaterialesQuery(String nombre, String codigo, Categoria categoria, Presentacion presentacion) {

    public static ConsultarMaterialesQuery sinFiltros() {
        return new ConsultarMaterialesQuery(null, null, null, null);
    }
}
