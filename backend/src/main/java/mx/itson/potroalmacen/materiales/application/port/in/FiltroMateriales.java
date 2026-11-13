/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.materiales.application.port.in;

import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;

/**
 *
 * @author erika
 * Filtros del listado de materiales
 *
 * Los textos en blanco se normalizan a null, que significa "sin filtro".
 */
public record FiltroMateriales(String busqueda, Categoria categoria, Campus campus) {

    public FiltroMateriales {
        busqueda = (busqueda == null || busqueda.isBlank()) ? null : busqueda.trim();
    }

    public static FiltroMateriales sinFiltros() {
        return new FiltroMateriales(null, null, null);
    }
}
