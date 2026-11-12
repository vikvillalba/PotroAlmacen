package mx.itson.potroalmacen.materiales.application.service;

import java.util.Objects;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarMaterialCommand;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarMaterialUseCase;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.exceptions.CodigoDuplicadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del caso de uso RegistrarMaterialUseCase.
 * Valida la unicidad del código del material y registra la existencia inicial en el campus elegido.
 */
@Service
@Transactional
public class RegistrarMaterialService implements RegistrarMaterialUseCase {

    private final MaterialRepositoryPort materialRepository;

    public RegistrarMaterialService(MaterialRepositoryPort materialRepository) {
        this.materialRepository = Objects.requireNonNull(materialRepository, "materialRepository es obligatorio");
    }

    @Override
    public Material registrar(RegistrarMaterialCommand comando) {
        Objects.requireNonNull(comando, "El comando no puede ser nulo");

        CodigoMaterial codigo = new CodigoMaterial(comando.codigo());

        if (materialRepository.existePorCodigo(codigo)) {
            throw new CodigoDuplicadoException(codigo.toString());
        }

        Material material = Material.crear(
                codigo,
                comando.nombre(),
                comando.categoria(),
                comando.unidadMedida(),
                comando.presentacion(),
                comando.contenidoPresentacion(),
                comando.campus(),
                comando.precioUnitario(),
                comando.stockMinimo(),
                comando.stockMaximo(),
                comando.usoPrecauciones());

        if (comando.existenciaInicial() > 0) {
            material.registrarEntrada(comando.existenciaInicial());
        }

        return materialRepository.guardar(material);
    }
}
