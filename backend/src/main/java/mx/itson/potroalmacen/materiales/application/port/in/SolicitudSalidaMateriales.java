package mx.itson.potroalmacen.materiales.application.port.in;

import java.util.List;

/**
 * Solicitud de un listado de materiales cuya existencia se consulta y se descuenta.
 *
 * <p>Es lo que Salidas le envía a Materiales. Solo transporta datos: que el material exista, que la cantidad sea
 * mayor a cero y que alcance la existencia lo verifica el caso de uso de Materiales.
 *
 * @param lineas materiales y cantidades solicitadas; nunca es {@code null}.
 */
public record SolicitudSalidaMateriales(List<LineaSalida> lineas) {

    public SolicitudSalidaMateriales {
        lineas = List.copyOf(lineas);
    }

    /**
     * Una línea de la solicitud: el material y la cantidad que sale.
     *
     * @param codigoMaterial código del material en el catálogo de Materiales.
     * @param cantidad unidades que salen.
     */
    public record LineaSalida(String codigoMaterial, int cantidad) {

        public LineaSalida {
            codigoMaterial = (codigoMaterial == null || codigoMaterial.isBlank()) ? null : codigoMaterial.trim();
        }
    }
}
