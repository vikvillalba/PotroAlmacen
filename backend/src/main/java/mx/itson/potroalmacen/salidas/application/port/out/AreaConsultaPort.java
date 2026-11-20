package mx.itson.potroalmacen.salidas.application.port.out;

/**
 * Puerto de salida para validar materiales contra el catálogo de un área.
 *
 * <p>Las áreas son del módulo Áreas: el adaptador que implemente este puerto le pide a ese módulo que haga la
 * validación. Salidas solo usa tipos propios y no depende de las clases de Áreas.
 */
public interface AreaConsultaPort {

    /**
     * Valida que un material esté permitido para un área y que la cantidad corresponda a lo asignado.
     *
     * @param idArea id del área que solicita el material.
     * @param codigoMaterial código del material solicitado.
     * @param cantidad unidades solicitadas; mayor a cero.
     */
    void validarMaterialPermitido(Integer idArea, String codigoMaterial, int cantidad);
}