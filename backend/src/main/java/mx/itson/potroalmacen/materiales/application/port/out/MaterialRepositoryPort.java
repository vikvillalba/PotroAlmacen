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
     * Busca materiales cuyo nombre o código contenga el texto de búsqueda (sin distinguir mayúsculas) y que
     * coincidan con la categoría y el campus indicados. Los campos nulos del filtro no se aplican.
     *
     * @param filtro filtros opcionales de búsqueda, categoría y campus.
     * @return los materiales que cumplen los filtros, ordenados por nombre; vacía si ninguno coincide.
     */
    List<Material> buscar(FiltroMateriales filtro);
}
