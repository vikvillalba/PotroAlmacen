package mx.itson.potroalmacen.materiales.application.port.out;

import java.util.List;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.FiltroMateriales;
import mx.itson.potroalmacen.materiales.domain.Material;

public interface MaterialRepositoryPort {

    /**
     * <p>Guarda un material en el sistema (tanto creación como actualización).</p>
     *
     * @param material Entidad de dominio a persistir.
     * @return El material guardado (con ID asignado si es nuevo).
     * @throws mx.itson.potroalmacen.materiales.domain.exceptions.CodigoDuplicadoException si otro material ya
     *     tiene el mismo código (por ejemplo, dos registros simultáneos).
     */
    Material guardar(Material material);

    /**
     * <p>Busca un material utilizando su objeto de valor CodigoMaterial.</p>
     *
     * @param codigo El código único del material.
     * @return Optional con el material si se encuentra, o vacío si no.
     */
    Optional<Material> buscarPorCodigo(CodigoMaterial codigo);

    /**
     * <p>Verifica si ya existe un material registrado con el código dado.</p>
     * (Requerimiento de la HU1 para evitar duplicados).
     *
     * @param codigo El código único a verificar.
     * @return true si ya existe, false en caso contrario.
     */
    boolean existePorCodigo(CodigoMaterial codigo);

    /**
     * @return todos los materiales, ordenados por nombre.
     */
    List<Material> buscarTodos();

    /**
     * Busca los materiales que cumplen el filtro, con la misma semántica que {@link FiltroMateriales#aplicaA}.
     *
     * @param filtro criterios de búsqueda.
     * @return los materiales que coinciden, ordenados por nombre.
     */
    List<Material> buscar(FiltroMateriales filtro);
}
