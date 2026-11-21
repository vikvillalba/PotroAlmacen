package mx.itson.potroalmacen.materiales.application.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.itson.potroalmacen.materiales.application.port.in.DescontarExistenciasUseCase;
import mx.itson.potroalmacen.materiales.application.port.in.ExistenciaMaterial;
import mx.itson.potroalmacen.materiales.application.port.in.SalidaRegistrada;
import mx.itson.potroalmacen.materiales.application.port.in.SolicitudSalidaMateriales;
import mx.itson.potroalmacen.materiales.application.port.in.SolicitudSalidaMateriales.LineaSalida;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.exceptions.ExistenciaInsuficienteException;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de {@link DescontarExistenciasUseCase}.
 *
 * <p>Descontar es todo o nada: primero se buscan y se verifican todos los materiales sin modificar ninguno, y solo
 * cuando todos alcanzan se descuentan y se guardan. Todo ocurre en una transacción, así que si el guardado falla a la
 * mitad, el inventario vuelve a como estaba.
 */
@Service
@Transactional
public class DescontarExistenciasService implements DescontarExistenciasUseCase {

    private final MaterialRepositoryPort repositorio;

    public DescontarExistenciasService(MaterialRepositoryPort repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio es obligatorio");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExistenciaMaterial> consultarExistencias(List<String> codigosMaterial) {
        if (codigosMaterial == null || codigosMaterial.isEmpty()) {
            throw new MaterialInvalidoException("Debe indicar al menos un material");
        }
        return codigosMaterial.stream()
                .map(codigo -> aExistencia(buscar(new CodigoMaterial(codigo))))
                .toList();
    }

    @Override
    public List<SalidaRegistrada> descontarExistencias(SolicitudSalidaMateriales solicitud) {
        if (solicitud == null || solicitud.lineas().isEmpty()) {
            throw new MaterialInvalidoException("Debe indicar al menos un material");
        }
        solicitud.lineas().forEach(DescontarExistenciasService::validarLinea);

        // Un código repetido se descuenta una sola vez, con la suma de sus cantidades.
        Map<CodigoMaterial, Integer> cantidadPorCodigo = new LinkedHashMap<>();
        for (LineaSalida linea : solicitud.lineas()) {
            cantidadPorCodigo.merge(new CodigoMaterial(linea.codigoMaterial()), linea.cantidad(), Math::addExact);
        }

        // Fase 1: buscar y verificar TODOS los materiales sin modificar ninguno. Si alguno no existe o no alcanza, se
        // lanza aquí y no se descontó nada (aunque el repositorio devuelva la misma instancia que guarda en memoria).
        Map<CodigoMaterial, Material> materiales = new LinkedHashMap<>();
        cantidadPorCodigo.forEach((codigo, cantidad) -> {
            Material material = buscar(codigo);
            if (cantidad > material.getCantidad()) {
                throw new ExistenciaInsuficienteException(codigo.valor(), material.getCantidad(), cantidad);
            }
            materiales.put(codigo, material);
        });

        // Fase 2: descontar y guardar. Ya se verificó la existencia, así que el dominio no debería rechazar nada.
        Map<CodigoMaterial, Material> guardados = new LinkedHashMap<>();
        materiales.forEach((codigo, material) -> {
            material.registrarSalida(cantidadPorCodigo.get(codigo));
            guardados.put(codigo, repositorio.guardar(material));
        });

        List<SalidaRegistrada> salidas = new ArrayList<>();
        for (LineaSalida linea : solicitud.lineas()) {
            Material material = guardados.get(new CodigoMaterial(linea.codigoMaterial()));
            salidas.add(new SalidaRegistrada(
                    material.getCodigo().valor(), material.getNombre(), linea.cantidad()));
        }
        return salidas;
    }

    private Material buscar(CodigoMaterial codigo) {
        return repositorio.buscarPorCodigo(codigo).orElseThrow(() -> new MaterialNoEncontradoException(codigo.valor()));
    }

    private static ExistenciaMaterial aExistencia(Material material) {
        return new ExistenciaMaterial(
                material.getCodigo().valor(),
                material.getNombre(),
                material.getUnidadMedida().getAbreviatura(),
                material.getCantidad());
    }

    /** Se valida antes de tocar el inventario para no descontar nada de una solicitud mal formada. */
    private static void validarLinea(LineaSalida linea) {
        if (linea.codigoMaterial() == null) {
            throw new MaterialInvalidoException("El código del material es obligatorio");
        }
        if (linea.cantidad() <= 0) {
            throw new MaterialInvalidoException(
                    "La cantidad de salida del material " + linea.codigoMaterial() + " debe ser mayor a cero");
        }
    }
}