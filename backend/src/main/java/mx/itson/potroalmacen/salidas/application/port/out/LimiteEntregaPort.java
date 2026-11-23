package mx.itson.potroalmacen.salidas.application.port.out;

import java.util.Optional;
import mx.itson.potroalmacen.salidas.domain.LimiteEntregaMaterial;

/**
 * Puerto de salida hacia Materiales para conocer el límite de entrega por empleado de un material. Materiales es el
 * dueño de ese dato; Salidas solo lo consulta.
 */
public interface LimiteEntregaPort {

    /**
     * @param codigoMaterial código del material.
     * @return el límite, o vacío si el material no tiene límite de entrega.
     */
    Optional<LimiteEntregaMaterial> buscarLimite(String codigoMaterial);
}
