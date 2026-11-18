package mx.itson.potroalmacen.salidas.domain;

public enum EstadoBoleta {
    CREADA("Creada"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada");

    private final String etiqueta;

    EstadoBoleta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
