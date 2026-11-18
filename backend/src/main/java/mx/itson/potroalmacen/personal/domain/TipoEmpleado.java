package mx.itson.potroalmacen.personal.domain;

public enum TipoEmpleado {
    ITSON("ITSON"),
    EXTERNO("Externo");

    private final String etiqueta;

    TipoEmpleado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
