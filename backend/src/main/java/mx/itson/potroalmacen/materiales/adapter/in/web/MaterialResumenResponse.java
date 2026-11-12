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
public record MaterialResumenResponse (
        Integer id,
        String codigo,
        String nombre,
        String categoria,
        String unidadMedida,
        String presentacion,
        int cantidad,
        int stockMinimo){
    
    public static MaterialResumenResponse de(Material m) {
        return new MaterialResumenResponse(
                m.getId(),
                m.getCodigo().valor(),
                m.getNombre(),
                m.getCategoria(),
                m.getUnidadMedida(),
                m.getPresentacion(),
                m.getCantidad(),
                m.getStockMinimo());
    }
}
