package mx.itson.potroalmacen.materiales.application.port.out;

import java.util.List;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.application.port.in.FiltroMateriales;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
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
     * <p>Busca materiales cuyo nombre, código o categoría contenga el texto de búsqueda,
     * y que coincidan con los filtros opcionales de categoría y campus.</p>
     *
     * @param filtro Filtros opcionales (los campos nulos no se aplican).
     * @return Lista de materiales que cumplen los filtros; vacía si ninguno coincide.
     */
    List<Material> buscar(FiltroMateriales filtro);
}
