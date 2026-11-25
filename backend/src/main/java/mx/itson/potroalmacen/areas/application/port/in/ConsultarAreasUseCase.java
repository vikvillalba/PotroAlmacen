package mx.itson.potroalmacen.areas.application.port.in;

import java.util.List;
import mx.itson.potroalmacen.areas.domain.Area;

public interface ConsultarAreasUseCase {
    List<Area> listarTodas();
}
