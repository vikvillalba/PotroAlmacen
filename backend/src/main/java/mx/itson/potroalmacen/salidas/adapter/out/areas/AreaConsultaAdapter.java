package mx.itson.potroalmacen.salidas.adapter.out.areas;

import java.util.Objects;
import mx.itson.potroalmacen.areas.application.port.in.SolicitudMaterialArea;
import mx.itson.potroalmacen.areas.application.port.in.ValidarMaterialAreaUseCase;
import mx.itson.potroalmacen.salidas.application.port.out.AreaConsultaPort;
import org.springframework.stereotype.Component;

/**
 * Implementa {@link AreaConsultaPort} pidiéndole la validación al módulo Áreas.
 *
 * <p>Convierte los datos de Salidas a la solicitud que entiende Áreas. Las excepciones que lance Áreas (área no
 * encontrada, material no permitido, cantidad inválida o excedida) se propagan sin cambios.
 */
@Component
public class AreaConsultaAdapter implements AreaConsultaPort {

    private final ValidarMaterialAreaUseCase validarMaterialArea;

    public AreaConsultaAdapter(ValidarMaterialAreaUseCase validarMaterialArea) {
        this.validarMaterialArea = Objects.requireNonNull(validarMaterialArea, "validarMaterialArea es obligatorio");
    }

    @Override
    public void validarMaterialPermitido(Integer idArea, String codigoMaterial, int cantidad) {
        validarMaterialArea.validar(new SolicitudMaterialArea(idArea, codigoMaterial, cantidad));
    }
}