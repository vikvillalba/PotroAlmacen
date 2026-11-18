package mx.itson.potroalmacen.areas.domain;

public enum FrecuenciaLimpieza {
    DOS_VECES_AL_DIA("2 veces al dia"),
    TRES_VECES_AL_DIA("3 veces al día"),
    DIARIA("Diaria"),
    CADA_TRES_DIAS("Cada 3 días"),
    SEMANAL("Cada semana"),
    QUINCENAL("Quincenal"),
    MENSUAL("Mensual");

    private final String descripcion;

    FrecuenciaLimpieza(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
