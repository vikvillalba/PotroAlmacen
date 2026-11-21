package mx.itson.potroalmacen.areas.application.port.in;

import java.util.List;

public interface ValidarMaterialAreaUseCase {

    List<MaterialPermitidoResumen> consultarMaterialesPermitidos(Integer idArea);

    void validar(SolicitudMaterialArea solicitud);
}
