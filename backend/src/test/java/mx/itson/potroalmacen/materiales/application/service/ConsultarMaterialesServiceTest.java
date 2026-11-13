package mx.itson.potroalmacen.materiales.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import mx.itson.potroalmacen.materiales.application.port.in.FiltroMateriales;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConsultarMaterialesServiceTest {

    private ConsultarMaterialesService service;
    private FakeMaterialRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FakeMaterialRepository();
        service = new ConsultarMaterialesService(repository);

        guardar("PIN-001", "Pintura vinílica blanca", Categoria.PINTURA, Campus.OBREGON_NAINARI);
        guardar("PIN-002", "Pintura de esmalte negra", Categoria.PINTURA, Campus.GUAYMAS);
        guardar("FER-001", "Tornillo hexagonal", Categoria.FERRETERIA, Campus.OBREGON_NAINARI);
    }

    @Test
    @DisplayName("Sin filtros (o con filtro nulo) lista todos, ordenados por nombre")
    void listar_sinFiltros() {
        List<String> todos = List.of("PIN-002", "PIN-001", "FER-001");
        assertEquals(todos, codigos(service.listar(FiltroMateriales.sinFiltros())));
        assertEquals(todos, codigos(service.listar(null)));
        assertEquals(todos, codigos(service.listar(new FiltroMateriales("   ", null, null))));
    }

    @Test
    @DisplayName("La búsqueda coincide con el nombre o el código, sin distinguir mayúsculas")
    void listar_porBusqueda() {
        assertEquals(List.of("FER-001"), codigos(service.listar(new FiltroMateriales("HEXA", null, null))));
        assertEquals(List.of("FER-001"), codigos(service.listar(new FiltroMateriales("fer-", null, null))));
    }

    @Test
    @DisplayName("Filtra por categoría")
    void listar_porCategoria() {
        assertEquals(
                List.of("PIN-002", "PIN-001"),
                codigos(service.listar(new FiltroMateriales(null, Categoria.PINTURA, null))));
    }

    @Test
    @DisplayName("Filtra por campus")
    void listar_porCampus() {
        assertEquals(
                List.of("PIN-001", "FER-001"),
                codigos(service.listar(new FiltroMateriales(null, null, Campus.OBREGON_NAINARI))));
    }

    @Test
    @DisplayName("Combina los filtros con Y")
    void listar_filtrosCombinados() {
        assertEquals(
                List.of("PIN-001"),
                codigos(service.listar(new FiltroMateriales("pintura", Categoria.PINTURA, Campus.OBREGON_NAINARI))));
        assertTrue(service.listar(new FiltroMateriales("tornillo", Categoria.PINTURA, null))
                .isEmpty());
    }

    private void guardar(String codigo, String nombre, Categoria categoria, Campus campus) {
        repository.guardar(Material.crear(
                new CodigoMaterial(codigo),
                nombre,
                categoria,
                UnidadMedida.PIEZA,
                Presentacion.PIEZA_INDIVIDUAL,
                null,
                campus,
                BigDecimal.ONE,
                0,
                null,
                null));
    }

    private static List<String> codigos(List<Material> materiales) {
        return materiales.stream().map(m -> m.getCodigo().valor()).toList();
    }
}
