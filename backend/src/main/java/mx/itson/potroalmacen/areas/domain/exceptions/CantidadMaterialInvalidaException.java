package mx.itson.potroalmacen.areas.domain.exceptions;

public class CantidadMaterialInvalidaException extends IllegalArgumentException {

    public static final String CODIGO = "CANTIDAD_MATERIAL_INVALIDA";

    private final String codigoMaterial;

    public CantidadMaterialInvalidaException(String codigoMaterial) {
        super("La cantidad del material " + codigoMaterial + " debe ser mayor a cero.");
        this.codigoMaterial = codigoMaterial;
    }

    public String getCodigoMaterial() {
        return codigoMaterial;
    }
}
