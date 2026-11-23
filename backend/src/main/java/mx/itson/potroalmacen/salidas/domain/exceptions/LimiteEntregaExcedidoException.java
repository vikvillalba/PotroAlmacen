package mx.itson.potroalmacen.salidas.domain.exceptions;

/**
 * Se lanza cuando la cantidad solicitada de un material supera el límite de entrega configurado para el empleado
 * dentro del periodo establecido.
 */
public class LimiteEntregaExcedidoException extends IllegalArgumentException {

    public static final String CODIGO = "LIMITE_ENTREGA_EXCEDIDO";

    private final String codigoMaterial;
    private final int limiteMaximo;
    private final int periodoDias;
    private final int cantidadEntregada;
    private final int cantidadSolicitada;

    public LimiteEntregaExcedidoException(
            String codigoMaterial, int limiteMaximo, int periodoDias, int cantidadEntregada, int cantidadSolicitada) {
        super(String.format(
                "El material %s excede el límite de entrega para el empleado. Límite: %d en %d días. Ya entregadas: %d, solicitadas: %d.",
                codigoMaterial, limiteMaximo, periodoDias, cantidadEntregada, cantidadSolicitada));
        this.codigoMaterial = codigoMaterial;
        this.limiteMaximo = limiteMaximo;
        this.periodoDias = periodoDias;
        this.cantidadEntregada = cantidadEntregada;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public String getCodigoMaterial() {
        return codigoMaterial;
    }

    public int getLimiteMaximo() {
        return limiteMaximo;
    }

    public int getPeriodoDias() {
        return periodoDias;
    }

    public int getCantidadEntregada() {
        return cantidadEntregada;
    }

    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }
}
