package mx.itson.potroalmacen.materiales.domain;

/** Campus donde se almacena un material. */
public enum Campus {
    OBREGON_NAINARI("Obregón Náinari"),
    OBREGON_CENTRO("Obregón Centro"),
    NAVOJOA_SUR("Navojoa Sur"),
    GUAYMAS("Guaymas"),
    EMPALME("Empalme");

    private final String etiqueta;

    Campus(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra en pantalla. */
    public String getEtiqueta() {
        return etiqueta;
    }
}
