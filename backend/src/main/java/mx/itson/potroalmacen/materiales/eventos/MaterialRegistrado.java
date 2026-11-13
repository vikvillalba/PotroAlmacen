package mx.itson.potroalmacen.materiales.eventos;

import java.time.Instant;
import java.util.Objects;

/**
 * Se publica cuando se registra un material nuevo en el catálogo.
 *
 * @param materialId id asignado al material.
 * @param codigo código único del material (por ejemplo, {@code PIN-001}).
 * @param nombre nombre del material.
 * @param campus campus donde se registró (nombre del enum {@code Campus}, por ejemplo {@code OBREGON_NAINARI}).
 * @param existenciaInicial cantidad con la que se registró; 0 si no tuvo existencia inicial.
 * @param ocurridoEn momento del registro.
 */
public record MaterialRegistrado(
        Integer materialId, String codigo, String nombre, String campus, int existenciaInicial, Instant ocurridoEn) {

    public MaterialRegistrado {
        Objects.requireNonNull(materialId, "materialId es obligatorio");
        Objects.requireNonNull(codigo, "codigo es obligatorio");
        Objects.requireNonNull(nombre, "nombre es obligatorio");
        Objects.requireNonNull(campus, "campus es obligatorio");
        Objects.requireNonNull(ocurridoEn, "ocurridoEn es obligatorio");
    }
}
