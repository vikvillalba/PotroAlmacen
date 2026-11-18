package mx.itson.potroalmacen.personal.domain;

public record AreaRef(Integer idArea, String nombreArea) {
    public AreaRef {
        if (idArea == null) {
            throw new IllegalArgumentException("El ID del área es obligatorio para la referencia.");
        }
        if (nombreArea == null || nombreArea.isBlank()) {
            throw new IllegalArgumentException("El nombre del área es obligatorio para la referencia.");
        }
    }
}
