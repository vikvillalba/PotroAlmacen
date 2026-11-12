package mx.itson.potroalmacen.materiales.domain;

/** Unidad en la que se cuenta la existencia de un material. */
public enum UnidadMedida {
    PIEZA("Pieza", "pza"),
    LITRO("Litro", "L"),
    KILOGRAMO("Kilogramo", "kg"),
    METRO_CUBICO("Metro cúbico", "m³");

    private final String etiqueta;
    private final String abreviatura;

    UnidadMedida(String etiqueta, String abreviatura) {
        this.etiqueta = etiqueta;
        this.abreviatura = abreviatura;
    }

    /** Texto que se muestra en pantalla. */
    public String getEtiqueta() {
        return etiqueta;
    }

    public String getAbreviatura() {
        return abreviatura;
    }
}
