package mx.itson.potroalmacen.salidas.application.port.in;

import java.util.List;
import mx.itson.potroalmacen.salidas.domain.Boleta;

public interface ConsultarBoletasUseCase {
    List<Boleta> listar();

    Boleta obtenerPorId(Integer id);
}
