package mx.itson.potroalmacen.areas.domain;

public record Espacio(String nombreEspacio, String descripcion, FrecuenciaLimpieza frecuenciaLimpieza) {
    public Espacio {
        if (nombreEspacio == null || nombreEspacio.isBlank()) {
            throw new IllegalArgumentException("El nombre del espacio es obligatorio.");
        }
        if (frecuenciaLimpieza == null) {
            throw new IllegalArgumentException("La frecuencia de limpieza es obligatoria.");
        }
    }
}
