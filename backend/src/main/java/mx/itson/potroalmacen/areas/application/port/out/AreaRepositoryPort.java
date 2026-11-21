package mx.itson.potroalmacen.areas.application.port.out;

import java.util.List;
import java.util.Optional;
import mx.itson.potroalmacen.areas.domain.Area;

/**
 * Puerto de salida para consultar y persistir áreas en la base de datos.
 */
public interface AreaRepositoryPort {

    /**
     * Busca un área por su identificador único.
     *
     * @param id identificador del área a consultar.
     * @return el área encontrada envuelta en {@link Optional}, o vacío si no existe.
     */
    Optional<Area> buscarPorId(Integer id);

    /**
     * Obtiene todas las áreas registradas.
     *
     * @return lista con las áreas encontradas.
     */
    List<Area> listarTodas();

    /**
     * Guarda o actualiza un área en la base de datos.
     *
     * @param area agregado de dominio {@link Area} a persistir.
     * @return el área persistida con su estado actualizado.
     */
    Area guardar(Area area);
}
