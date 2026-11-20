package mx.itson.potroalmacen.areas.domain.exceptions;

public class MaterialNoPermitidoEnAreaException extends IllegalArgumentException {

    public static final String CODIGO = "MATERIAL_NO_PERMITIDO_EN_AREA";

    private final String codigoMaterial;

    public MaterialNoPermitidoEnAreaException(String codigoMaterial) {
        super("El material no está catalogado para esta área");
        this.codigoMaterial = codigoMaterial;
    }

    public String getCodigoMaterial() {
        return codigoMaterial;
    }
}