/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.materiales.application.port.in;

/**
 *
 * @author erika
 * Filtros del listado de materiales 
 * 
 * Los textos en blanco se normalizan a null, que significa "sin filtro".
 */
public record FiltroMateriales (String busqueda, String categoria){
    public FiltroMateriales {
        busqueda = normalizar(busqueda);
        categoria = normalizar(categoria);
    }
 
    public static FiltroMateriales sinFiltros() {
        return new FiltroMateriales(null, null);
    }
 
    private static String normalizar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
