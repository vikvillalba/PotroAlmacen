package mx.itson.potroalmacen.materiales.application.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.FiltroMateriales;
import mx.itson.potroalmacen.materiales.domain.Material;

/** Repositorio falso en memoria para pruebas unitarias sin Spring ni base de datos. */
class FakeMaterialRepository implements MaterialRepositoryPort {

    private static final Comparator<Material> POR_NOMBRE =
            Comparator.comparing(Material::getNombre).thenComparing(m -> m.getCodigo().valor());

    private final Map<String, Material> storage = new LinkedHashMap<>();
    private int idSecuencia = 1;

    @Override
    public Material guardar(Material material) {
        Material guardado = material.getId() == null ? material.conId(idSecuencia++) : material;
        storage.put(guardado.getCodigo().toString(), guardado);
        return guardado;
    }

    @Override
    public Optional<Material> buscarPorCodigo(CodigoMaterial codigo) {
        if (codigo == null) return Optional.empty();
        return Optional.ofNullable(storage.get(codigo.toString()));
    }

    @Override
    public boolean existePorCodigo(CodigoMaterial codigo) {
        if (codigo == null) return false;
        return storage.containsKey(codigo.toString());
    }

    @Override
    public List<Material> buscarTodos() {
        return storage.values().stream().sorted(POR_NOMBRE).toList();
    }

    @Override
    public List<Material> buscar(FiltroMateriales filtro) {
        return storage.values().stream().filter(filtro::aplicaA).sorted(POR_NOMBRE).toList();
    }
}
