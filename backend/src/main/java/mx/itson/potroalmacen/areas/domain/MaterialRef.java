package mx.itson.potroalmacen.areas.domain;

public record MaterialRef(String codigoMaterial, String nombreMaterial, int cantidad) {
    public MaterialRef {
        if (codigoMaterial == null || codigoMaterial.isBlank()) {
            throw new IllegalArgumentException("El código del material es obligatorio.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del material debe ser mayor a cero.");
        }
    }
}
