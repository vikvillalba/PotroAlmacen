package mx.itson.potroalmacen.areas.adapter.in.web;

import mx.itson.potroalmacen.areas.domain.Area;

public record AreaResumenResponse(Integer id, String nombreArea, String tipoArea) {

    public static AreaResumenResponse de(Area area) {
        return new AreaResumenResponse(
                area.getId(),
                area.getNombreArea(),
                area.getTipoArea() != null ? area.getTipoArea().name() : null);
    }
}
