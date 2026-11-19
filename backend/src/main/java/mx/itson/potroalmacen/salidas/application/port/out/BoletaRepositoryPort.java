package mx.itson.potroalmacen.salidas.application.port.out;

import mx.itson.potroalmacen.salidas.domain.Boleta;

public interface BoletaRepositoryPort {
    /**
     * Registra una boleta entregada
     * @param boleta Boleta a registrar
     * @return Boleta registrada
     */
    Boleta guardar(Boleta boleta);
}
