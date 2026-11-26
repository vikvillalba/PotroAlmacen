package mx.itson.potroalmacen.salidas.application.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import mx.itson.potroalmacen.salidas.domain.Boleta;

public interface BoletaRepositoryPort {
    /**
     * Registra una boleta entregada
     * @param boleta Boleta a registrar
     * @return Boleta registrada
     */
    Boleta guardar(Boleta boleta);

    /**
     * Lista todas las boletas registradas ordenadas por fecha descendente.
     */
    List<Boleta> listarTodas();

    /**
     * Busca una boleta por su identificador.
     */
    Optional<Boleta> buscarPorId(Integer id);

    /**
     * Suma las unidades de un material que se le han entregado a un empleado desde una fecha. Solo cuentan las boletas
     * completadas (entregas que sí ocurrieron).
     *
     * @param solicitanteId id del empleado que recibió el material.
     * @param codigoMaterial código del material.
     * @param desde inicio del periodo, inclusive.
     * @return unidades entregadas; 0 si no recibió ninguna.
     */
    int cantidadEntregada(Integer solicitanteId, String codigoMaterial, LocalDateTime desde);
}
