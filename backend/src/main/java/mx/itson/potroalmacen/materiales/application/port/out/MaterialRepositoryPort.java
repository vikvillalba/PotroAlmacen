package mx.itson.potroalmacen.materiales.application.port.out;

import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;

import java.util.Optional;

public interface MaterialRepositoryPort {

    /**
     <p>Guarda un material en el sistema (tanto creación como actualización).</p>
     *
     * @param material Entidad de dominio a persistir.
     * @return El material guardado (con ID asignado si es nuevo).
     */
    Material guardar(Material material);

    /**
     <p>Busca un material utilizando su objeto de valor CodigoMaterial.</p>
     *
     * @param codigo El código único del material.
     * @return Optional con el material si se encuentra, o vacío si no.
     */
    Optional<Material> buscarPorCodigo(CodigoMaterial codigo);

    /**
     <p>Verifica si ya existe un material registrado con el código dado.</p>
     * (Requerimiento de la HU1 para evitar duplicados).
     *
     * @param codigo El código único a verificar.
     * @return true si ya existe, false en caso contrario.
     */
    boolean existePorCodigo(CodigoMaterial codigo);
}