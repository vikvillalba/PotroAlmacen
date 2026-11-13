package mx.itson.potroalmacen.materiales.application.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.application.port.in.FiltroMateriales;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import java.util.Objects;

/** Repositorio falso en memoria para pruebas unitarias sin Spring ni base de datos. */
class FakeMaterialRepository implements MaterialRepositoryPort {

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

    /** Misma semántica que la consulta JPQL de SpringDataMaterialRepository. */
    @Override
    public List<Material> buscar(FiltroMateriales filtro) {
        return storage.values().stream()
                .filter(m -> filtro.busqueda() == null
                        || contiene(m.getNombre(), filtro.busqueda())
                        || contiene(m.getCodigo().valor(), filtro.busqueda()))
                .filter(m -> filtro.categoria() == null || m.getCategoria() == filtro.categoria())
                .filter(m -> filtro.campus() == null || m.getCampus() == filtro.campus())
                .sorted(Comparator.comparing(Material::getNombre))
                .toList();
    }

    private static boolean contiene(String valor, String buscado) {
        return valor.toLowerCase().contains(buscado.toLowerCase());
    }

    @Override
    public Optional<Material> buscarPorId(Integer id) {
        if (id == null) return Optional.empty();
        // Cambia "repositorioInterno" por el nombre real de tu variable (ej. "materiales")
        return storage.values().stream()
                .filter(m -> java.util.Objects.equals(m.getId(), id))
                .findFirst();
    }
}
