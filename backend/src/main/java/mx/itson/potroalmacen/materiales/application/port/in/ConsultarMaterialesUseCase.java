package mx.itson.potroalmacen.materiales.application.port.in;

import java.util.List;
import mx.itson.potroalmacen.materiales.domain.Material;

/** Puerto de entrada para consultar el catálogo de materiales. */
public interface ConsultarMaterialesUseCase {

    /**
     * Consulta todos los materiales registrados.
     *
     * @return los materiales ordenados por nombre; lista vacía si no hay ninguno.
     */
    List<Material> consultarTodos();

    /**
     * Consulta los materiales que cumplen todos los criterios dados. Sin criterios equivale a
     * {@link #consultarTodos()}.
     *
     * @param consulta criterios de búsqueda (nombre, código, categoría y presentación); no puede ser nula.
     * @return los materiales que coinciden, ordenados por nombre; lista vacía si no hay coincidencias.
     */
    List<Material> consultarFiltrados(ConsultarMaterialesQuery consulta);
}
