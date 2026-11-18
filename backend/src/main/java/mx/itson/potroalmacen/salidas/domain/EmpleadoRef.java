package mx.itson.potroalmacen.boletas.domain;

public record EmpleadoRef(Integer idEmpleado, String nombreCompleto, String puesto) {
    public EmpleadoRef {
        if (idEmpleado == null) {
            throw new IllegalArgumentException("El ID del empleado es obligatorio para la referencia.");
        }
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException("El nombre del empleado es obligatorio.");
        }
    }
}
