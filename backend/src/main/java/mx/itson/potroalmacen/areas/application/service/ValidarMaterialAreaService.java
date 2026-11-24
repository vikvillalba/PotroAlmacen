package mx.itson.potroalmacen.areas.application.service;

import java.util.List;
import java.util.Objects;
import mx.itson.potroalmacen.areas.application.port.in.MaterialPermitidoResumen;
import mx.itson.potroalmacen.areas.application.port.in.SolicitudMaterialArea;
import mx.itson.potroalmacen.areas.application.port.in.ValidarMaterialAreaUseCase;
import mx.itson.potroalmacen.areas.application.port.out.AreaRepositoryPort;
import mx.itson.potroalmacen.areas.domain.Area;
import mx.itson.potroalmacen.areas.domain.MaterialRef;
import mx.itson.potroalmacen.areas.domain.exceptions.AreaNoEncontradaException;
import mx.itson.potroalmacen.areas.domain.exceptions.CantidadExcedeAsignacionAreaException;
import mx.itson.potroalmacen.areas.domain.exceptions.CantidadMaterialInvalidaException;
import mx.itson.potroalmacen.areas.domain.exceptions.MaterialNoPermitidoEnAreaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de {@link ValidarMaterialAreaUseCase}.
 *
 * <p>Valida la solicitud de un material para un área: el área debe existir, el material debe estar catalogado en
 * ella y la cantidad solicitada debe ser mayor a cero y no superar la cantidad asignada al área.
 */
@Service
@Transactional(readOnly = true)
public class ValidarMaterialAreaService implements ValidarMaterialAreaUseCase {

    private final AreaRepositoryPort areaRepository;

    public ValidarMaterialAreaService(AreaRepositoryPort areaRepository) {
        this.areaRepository = Objects.requireNonNull(areaRepository, "areaRepository es obligatorio");
    }

    @Override
    public List<MaterialPermitidoResumen> consultarMaterialesPermitidos(Integer idArea) {
        return buscarArea(idArea).getMateriales().stream()
                .map(material -> new MaterialPermitidoResumen(material.codigoMaterial(), material.nombreMaterial()))
                .toList();
    }

    @Override
    public void validar(SolicitudMaterialArea solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }
        if (solicitud.codigoMaterial() == null) {
            throw new IllegalArgumentException("El código del material es obligatorio.");
        }
        if (solicitud.cantidad() == null || solicitud.cantidad() <= 0) {
            throw new CantidadMaterialInvalidaException(solicitud.codigoMaterial());
        }

        Area area = buscarArea(solicitud.idArea());

        MaterialRef asignado = area.getMateriales().stream()
                .filter(material -> material.codigoMaterial().equals(solicitud.codigoMaterial()))
                .findFirst()
                .orElseThrow(() -> new MaterialNoPermitidoEnAreaException(solicitud.codigoMaterial()));

        if (solicitud.cantidad() > asignado.cantidad()) {
            throw new CantidadExcedeAsignacionAreaException(
                    asignado.codigoMaterial(), asignado.cantidad(), solicitud.cantidad());
        }
    }

    private Area buscarArea(Integer idArea) {
        if (idArea == null) {
            throw new IllegalArgumentException("El id del área es obligatorio.");
        }
        return areaRepository.buscarPorId(idArea).orElseThrow(() -> new AreaNoEncontradaException(idArea));
    }
}