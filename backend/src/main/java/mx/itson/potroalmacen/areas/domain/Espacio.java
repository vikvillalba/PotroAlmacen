package mx.itson.potroalmacen.areas.domain;

public record Espacio(
        TipoEspacio tipoEspacio,
        int cantidadEspacios
) {
    public Espacio {
        if (tipoEspacio == null) {
            throw new IllegalArgumentException("El tipo de espacio es obligatorio.");
        }
        if (cantidadEspacios <= 0) {
            throw new IllegalArgumentException("La cantidad de espacios debe ser mayor a cero.");
        }
    }
}