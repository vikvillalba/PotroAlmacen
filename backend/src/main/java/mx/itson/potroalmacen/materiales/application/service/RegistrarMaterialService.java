package mx.itson.potroalmacen.materiales.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarMaterialCommand;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarMaterialUseCase;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.application.port.out.PublicadorEventosPort;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.exceptions.CodigoDuplicadoException;
import mx.itson.potroalmacen.materiales.eventos.MaterialRegistrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del caso de uso RegistrarMaterialUseCase.
 * Valida la unicidad del código del material, registra la existencia inicial en el campus elegido
 * y publica el evento {@link MaterialRegistrado} para los suscriptores.
 */
@Service
@Transactional
public class RegistrarMaterialService implements RegistrarMaterialUseCase {

    private final MaterialRepositoryPort materialRepository;
    private final PublicadorEventosPort publicadorEventos;
    private final Clock reloj;

    @Autowired
    public RegistrarMaterialService(
            MaterialRepositoryPort materialRepository, PublicadorEventosPort publicadorEventos) {
        this(materialRepository, publicadorEventos, Clock.systemUTC());
    }

    RegistrarMaterialService(
            MaterialRepositoryPort materialRepository, PublicadorEventosPort publicadorEventos, Clock reloj) {
        this.materialRepository = Objects.requireNonNull(materialRepository, "materialRepository es obligatorio");
        this.publicadorEventos = Objects.requireNonNull(publicadorEventos, "publicadorEventos es obligatorio");
        this.reloj = Objects.requireNonNull(reloj, "reloj es obligatorio");
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
                comando.existenciaInicial(),
                comando.stockMinimo(),
                comando.stockMaximo(),
                comando.usoPrecauciones());

        Material guardado = materialRepository.guardar(material);

        // Se publica dentro de la transacción: los suscriptores con @ApplicationModuleListener
        // lo reciben solo si el registro hace commit.
        publicadorEventos.publicar(new MaterialRegistrado(
                guardado.getId(),
                guardado.getCodigo().toString(),
                guardado.getNombre(),
                guardado.getCampus().name(),
                guardado.getCantidad(),
                Instant.now(reloj)));

        return guardado;
    }
}
