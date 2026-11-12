package mx.itson.potroalmacen.materiales.domain;

/** Departamento al que pertenece una categoría de material. */
public enum Departamento {
    MANTENIMIENTO("Mantenimiento"),
    SERVICIOS_GENERALES("Servicios Generales");

    private final String etiqueta;

    Departamento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto que se muestra en pantalla. */
    public String getEtiqueta() {
        return etiqueta;
    }
}
