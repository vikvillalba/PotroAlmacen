package mx.itson.potroalmacen.boletas.domain;

public record MaterialRef(String codigoMaterial, String nombreMaterial) {
    public MaterialRef {
        if (codigoMaterial == null || codigoMaterial.isBlank()) {
            throw new IllegalArgumentException("El código del material es obligatorio.");
        }
    }
}
