package mx.itson.potroalmacen.areas.application.port.in;

import mx.itson.potroalmacen.areas.domain.Area;

public interface AsignarMaterialAreaUseCase {
    Area asignar(AsignarMaterialAreaCommand comando);
}
