/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mx.itson.potroalmacen.materiales.application.port.in;

import java.util.List;
import mx.itson.potroalmacen.materiales.domain.Material;

/**
 *
 * @author erika
 *
 * Caso de uso: listar materiales con búsqueda por nombre o código y filtro por categoría.
 */
public interface ConsultarMaterialesUseCase {
    /** Lista materiales con búsqueda por nombre o código y filtros por categoría y campus. */
    List<Material> listar(FiltroMateriales filtro);

    /**
     * Obtiene el detalle de un material.
     *
     * @throws MaterialNoEncontradoException si no existe
     */
    Material obtenerPorId(Integer id);
}
