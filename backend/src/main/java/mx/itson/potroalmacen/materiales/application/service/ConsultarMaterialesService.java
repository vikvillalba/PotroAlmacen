package mx.itson.potroalmacen.materiales.application.service;

import java.util.List;
import java.util.Objects;
import mx.itson.potroalmacen.materiales.application.port.in.ConsultarMaterialesQuery;
import mx.itson.potroalmacen.materiales.application.port.in.ConsultarMaterialesUseCase;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.FiltroMateriales;
import mx.itson.potroalmacen.materiales.domain.Material;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementación de {@link ConsultarMaterialesUseCase}. Solo lectura. */
@Service
@Transactional(readOnly = true)
public class ConsultarMaterialesService implements ConsultarMaterialesUseCase {

    private final MaterialRepositoryPort materialRepository;

    public ConsultarMaterialesService(MaterialRepositoryPort materialRepository) {
        this.materialRepository = Objects.requireNonNull(materialRepository, "materialRepository es obligatorio");
    }

    @Override
    public List<Material> consultarTodos() {
        return materialRepository.buscarTodos();
    }

    @Override
    public List<Material> consultarFiltrados(ConsultarMaterialesQuery consulta) {
        Objects.requireNonNull(consulta, "La consulta no puede ser nula");
        FiltroMateriales filtro = new FiltroMateriales(
                consulta.nombre(), consulta.codigo(), consulta.categoria(), consulta.presentacion());
        if (filtro.estaVacio()) {
            return materialRepository.buscarTodos();
        }
        return materialRepository.buscar(filtro);
    }
}
