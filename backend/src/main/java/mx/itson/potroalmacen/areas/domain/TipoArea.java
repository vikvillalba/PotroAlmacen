package mx.itson.potroalmacen.areas.domain;

public enum TipoArea {
    ACADEMICA("Académica"),
    ADMINISTRATIVA("Administrativa"),
    ALMACEN("Almacén"),
    LABORATORIO("Laboratorio"),
    INSTALACIONES_DEPORTIVAS("Instalaciones Deportivas");

    private final String etiqueta;

    TipoArea(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}