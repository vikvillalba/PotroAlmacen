package mx.itson.potroalmacen.areas.application.service;

import java.util.List;
import java.util.Objects;
import mx.itson.potroalmacen.areas.application.port.in.AsignarMaterialAreaCommand;
import mx.itson.potroalmacen.areas.application.port.in.AsignarMaterialAreaUseCase;
import mx.itson.potroalmacen.areas.application.port.in.ConsultarAreasUseCase;
import mx.itson.potroalmacen.areas.application.port.out.AreaRepositoryPort;
import mx.itson.potroalmacen.areas.domain.Area;
import mx.itson.potroalmacen.areas.domain.MaterialRef;
import mx.itson.potroalmacen.areas.domain.exceptions.AreaNoEncontradaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GestionarAreasService implements ConsultarAreasUseCase, AsignarMaterialAreaUseCase {

    private final AreaRepositoryPort areaRepository;

    public GestionarAreasService(AreaRepositoryPort areaRepository) {
        this.areaRepository = Objects.requireNonNull(areaRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Area> listarTodas() {
        return areaRepository.listarTodas();
    }

    @Override
    @Transactional
    public Area asignar(AsignarMaterialAreaCommand comando) {
        Objects.requireNonNull(comando, "El comando es obligatorio.");
        Area area = areaRepository
                .buscarPorId(comando.idArea())
                .orElseThrow(() -> new AreaNoEncontradaException(comando.idArea()));

        area.agregarMaterial(new MaterialRef(comando.codigoMaterial(), comando.nombreMaterial(), comando.cantidad()));

        return areaRepository.guardar(area);
    }
}
