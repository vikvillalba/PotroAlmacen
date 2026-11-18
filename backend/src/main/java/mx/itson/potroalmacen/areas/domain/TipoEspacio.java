package mx.itson.potroalmacen.areas.domain;

public enum TipoEspacio {
    AULA("Aula"),
    OFICINA("Oficina"),
    LABORATORIO("Laboratorio"),
    BAÑO("Baño"),
    BODEGA("Bodega"),
    CUBICULO("Cubículo"),
    SALA_JUNTA("Sala de Juntas");

    private final String etiqueta;

    TipoEspacio(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
