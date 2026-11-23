package mx.itson.potroalmacen.materiales.application.service;

import java.util.Objects;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.application.port.in.ConsultarLimiteEntregaUseCase;
import mx.itson.potroalmacen.materiales.application.port.in.LimiteEntrega;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementación de {@link ConsultarLimiteEntregaUseCase}. Solo lee; no modifica el material. */
@Service
@Transactional(readOnly = true)
public class ConsultarLimiteEntregaService implements ConsultarLimiteEntregaUseCase {

    private final MaterialRepositoryPort repositorio;

    public ConsultarLimiteEntregaService(MaterialRepositoryPort repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio es obligatorio");
    }

    @Override
    public Optional<LimiteEntrega> consultarLimiteEntrega(String codigoMaterial) {
        CodigoMaterial codigo = new CodigoMaterial(codigoMaterial);
        Material material = repositorio
                .buscarPorCodigo(codigo)
                .orElseThrow(() -> new MaterialNoEncontradoException(codigo.valor()));
        if (!material.tieneLimiteEntrega()) {
            return Optional.empty();
        }
        return Optional.of(new LimiteEntrega(
                material.getCodigo().valor(),
                material.getNombre(),
                material.getLimiteEntregaPorEmpleado(),
                material.getPeriodoLimiteDias()));
    }
}
