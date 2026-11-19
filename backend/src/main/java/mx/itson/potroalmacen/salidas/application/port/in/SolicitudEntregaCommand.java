package mx.itson.potroalmacen.salidas.application.port.in;

import java.util.List;

/**
 * Comando que modela la solicitud de entrega de un listado de materiales.
 *
 * <p>Lo recibe el caso de uso que genera la boleta de entrega. Solo transporta datos: las reglas de
 * negocio (solicitante y entregador distintos, al menos un material, etc.) las valida el dominio
 * ({@link mx.itson.potroalmacen.salidas.domain.Boleta}).
 *
 * @param idSolicitante id del empleado que solicita los materiales.
 * @param idEntregador id del empleado que entrega los materiales.
 * @param materiales listado de materiales solicitados; nunca es {@code null}.
 */
public record SolicitudEntregaCommand(
        Integer idSolicitante, Integer idEntregador, List<MaterialSolicitado> materiales) {

    public SolicitudEntregaCommand {
        materiales = List.copyOf(materiales);
    }

    /**
     * Una línea de la solicitud: el material, la cantidad que tiene en inventario y la cantidad a entregar.
     *
     * @param codigoMaterial código del material en el catálogo del módulo Materiales.
     * @param cantidadEnInventario existencia del material en inventario al momento de la solicitud.
     * @param cantidadAEntregar unidades que se van a entregar.
     */
    public record MaterialSolicitado(String codigoMaterial, Integer cantidadEnInventario, Integer cantidadAEntregar) {

        public MaterialSolicitado {
            codigoMaterial = (codigoMaterial == null || codigoMaterial.isBlank()) ? null : codigoMaterial.trim();
        }
    }
}
