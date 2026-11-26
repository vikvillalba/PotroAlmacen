package mx.itson.potroalmacen.materiales.application.service;

import java.util.Objects;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarSalidaMaterialUseCase;
import mx.itson.potroalmacen.materiales.application.port.in.SalidaRegistrada;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de {@link RegistrarSalidaMaterialUseCase}.
 *
 * <p>Busca el material por código y le pide al dominio registrar la salida, que verifica la existencia y la descuenta.
 */
@Service
@Transactional
public class RegistrarSalidaMaterialService implements RegistrarSalidaMaterialUseCase {

    private final MaterialRepositoryPort repositorio;

    public RegistrarSalidaMaterialService(MaterialRepositoryPort repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio es obligatorio");
    }

    @Override
    public SalidaRegistrada registrarSalida(String codigoMaterial, int cantidad) {
        CodigoMaterial codigo = new CodigoMaterial(codigoMaterial);
        Material material = repositorio
                .buscarPorCodigoConBloqueo(codigo)
                .orElseThrow(() -> new MaterialNoEncontradoException(codigo.valor()));

        material.registrarSalida(cantidad);
        Material guardado = repositorio.guardar(material);

        return new SalidaRegistrada(guardado.getCodigo().valor(), guardado.getNombre(), cantidad);
    }
}
