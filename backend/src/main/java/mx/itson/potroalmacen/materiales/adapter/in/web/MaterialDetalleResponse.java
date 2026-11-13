package mx.itson.potroalmacen.materiales.adapter.in.web;

import java.math.BigDecimal;
import mx.itson.potroalmacen.materiales.domain.Material;

/** Detalle de la ficha técnica de un material (respuesta de GET /api/materiales/{id}). */
public record MaterialDetalleResponse(
        Integer id,
        String codigo,
        String nombre,
        String categoriaId,
        String categoria,
        String departamento,
        String unidadMedida,
        String presentacion,
        String contenidoPresentacion,
        String campus,
        BigDecimal precioUnitario,
        int cantidad,
        int stockMinimo,
        Integer stockMaximo,
        String usoPrecauciones,
        String estado) {

    public static MaterialDetalleResponse de(Material m) {
        return new MaterialDetalleResponse(
                m.getId(),
                m.getCodigo().valor(),
                m.getNombre(),
                m.getCategoria().name(),
                m.getCategoria().getEtiqueta(),
                m.getDepartamento().getEtiqueta(),
                m.getUnidadMedida().getEtiqueta(),
                m.getPresentacion().getEtiqueta(),
                m.getContenidoPresentacion(),
                m.getCampus().getEtiqueta(),
                m.getPrecioUnitario(),
                m.getCantidad(),
                m.getStockMinimo(),
                m.getStockMaximo(),
                m.getUsoPrecauciones(),
                estado(m));
    }

    private static String estado(Material m) {
        if (m.getCantidad() == 0) {
            return "Agotado";
        }
        return m.estaBajoMinimo() ? "Stock bajo" : "Disponible";
    }
}