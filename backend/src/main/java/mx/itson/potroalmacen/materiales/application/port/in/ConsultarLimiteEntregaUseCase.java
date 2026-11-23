package mx.itson.potroalmacen.materiales.application.port.in;

import java.util.Optional;

/** Caso de uso: consultar el límite de entrega por empleado que tiene configurado un material. */
public interface ConsultarLimiteEntregaUseCase {

    /**
     * @param codigoMaterial código del material.
     * @return el límite del material, o vacío si el material no tiene límite de entrega.
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.MaterialNoEncontradoException si el material no
     *     existe.
     */
    Optional<LimiteEntrega> consultarLimiteEntrega(String codigoMaterial);
}
