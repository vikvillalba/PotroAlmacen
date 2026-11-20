package mx.itson.potroalmacen.areas.application.port.in;

public record SolicitudMaterialArea(Integer idArea, String codigoMaterial, Integer cantidad) {

    public SolicitudMaterialArea {
        codigoMaterial = (codigoMaterial == null || codigoMaterial.isBlank()) ? null : codigoMaterial.trim();
    }
}
