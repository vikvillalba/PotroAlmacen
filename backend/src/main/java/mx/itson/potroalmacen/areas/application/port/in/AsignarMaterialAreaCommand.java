package mx.itson.potroalmacen.areas.application.port.in;

public record AsignarMaterialAreaCommand(Integer idArea, String codigoMaterial, String nombreMaterial, int cantidad) {}
