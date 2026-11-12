package mx.itson.potroalmacen.materiales.domain;

/**
 * Forma en que viene empacado un material.
 *
 * <p>El contenido específico (por ejemplo "19 L" o "1 pieza = 1 cubeta de 19 L") va aparte, en
 * {@code contenidoPresentacion} del material.
 */
public enum Presentacion {
    PIEZA_INDIVIDUAL("Pieza individual"),
    BOTELLA("Botella"),
    GALON("Galón"),
    CUBETA("Cubeta"),
    BOLSA("Bolsa"),
    SACO("Saco"),
    CAJA("Caja"),
    PAQUETE("Paquete"),
    ROLLO("Rollo"),
    TUBO("Tubo"),
    JUEGO("Juego"),
    OTRA("Otra");

    private final String etiqueta;

    Presentacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra en pantalla. */
    public String getEtiqueta() {
        return etiqueta;
    }
}
