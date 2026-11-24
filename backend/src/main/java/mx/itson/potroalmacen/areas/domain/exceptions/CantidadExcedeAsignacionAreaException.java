package mx.itson.potroalmacen.areas.domain.exceptions;

public class CantidadExcedeAsignacionAreaException extends IllegalArgumentException {

    public static final String CODIGO = "CANTIDAD_EXCEDE_ASIGNACION_AREA";

    private final String codigoMaterial;
    private final int cantidadAsignada;
    private final int cantidadSolicitada;

    public CantidadExcedeAsignacionAreaException(
            String codigoMaterial, int cantidadAsignada, int cantidadSolicitada) {
        super(String.format(
                "La cantidad solicitada del material %s (%d) excede la cantidad asignada al área (%d).",
                codigoMaterial, cantidadSolicitada, cantidadAsignada));
        this.codigoMaterial = codigoMaterial;
        this.cantidadAsignada = cantidadAsignada;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public String getCodigoMaterial() { return codigoMaterial; }
    public int getCantidadAsignada() { return cantidadAsignada; }
    public int getCantidadSolicitada() { return cantidadSolicitada; }
}