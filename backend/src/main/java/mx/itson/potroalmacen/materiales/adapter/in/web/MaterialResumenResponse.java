/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.itson.potroalmacen.materiales.adapter.in.web;

import mx.itson.potroalmacen.materiales.domain.Material;

/**
 *
 * @author erika
 */
public record MaterialResumenResponse(
        Integer id,
        String codigo,
        String nombre,
        String categoriaId,
        String categoria,
        String unidadMedida,
        String presentacion,
        String contenidoPresentacion,
        String campus,
        int cantidad,
        int stockMinimo,
        String estado) {
 
    public static MaterialResumenResponse de(Material m) {
        return new MaterialResumenResponse(
                m.getId(),
                m.getCodigo().valor(),
                m.getNombre(),
                m.getCategoria().name(),
                m.getCategoria().toString(),
                m.getUnidadMedida().toString(),
                m.getPresentacion().toString(),
                m.getContenidoPresentacion(),
                m.getCampus().toString(),
                m.getCantidad(),
                m.getStockMinimo(),
                estado(m));
    }
 
    private static String estado(Material m) {
        if (m.getCantidad() == 0) {
            return "Agotado";
        }
        return m.estaBajoMinimo() ? "Stock bajo" : "Disponible";
    }
}
