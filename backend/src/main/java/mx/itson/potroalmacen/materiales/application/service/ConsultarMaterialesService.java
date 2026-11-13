package mx.itson.potroalmacen.materiales.application.service;

import java.util.List;
import java.util.Objects;
import mx.itson.potroalmacen.materiales.application.port.in.ConsultarMaterialesUseCase;
import mx.itson.potroalmacen.materiales.application.port.in.FiltroMateriales;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementación de {@link ConsultarMaterialesUseCase}. Solo lectura. */
@Service
@Transactional(readOnly = true)
public class ConsultarMaterialesService implements ConsultarMaterialesUseCase {

    private final MaterialRepositoryPort repositorio;

    public ConsultarMaterialesService(MaterialRepositoryPort repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio es obligatorio");
    }

    @Override
    public List<Material> listar(FiltroMateriales filtro) {
        FiltroMateriales efectivo = filtro == null ? FiltroMateriales.sinFiltros() : filtro;
        return repositorio.buscar(efectivo);
    }

    @Override
    public Material obtenerPorId(Integer id) {
        return repositorio.buscarPorId(id).orElseThrow(() -> new MaterialNoEncontradoException(id));
    }
}
