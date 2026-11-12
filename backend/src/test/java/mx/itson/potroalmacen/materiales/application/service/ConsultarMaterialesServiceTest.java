package mx.itson.potroalmacen.materiales.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import mx.itson.potroalmacen.materiales.application.port.in.ConsultarMaterialesQuery;
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

    private FakeMaterialRepository repository;
    private ConsultarMaterialesService service;

    @BeforeEach
    void setUp() {
        repository = new FakeMaterialRepository();
        service = new ConsultarMaterialesService(repository);

        guardar("PIN-001", "Pintura vinílica blanca", Categoria.PINTURA, Presentacion.CUBETA);
        guardar("PIN-002", "Pintura de esmalte negra", Categoria.PINTURA, Presentacion.GALON);
        guardar("FER-001", "Tornillo hexagonal", Categoria.FERRETERIA, Presentacion.CAJA);
        guardar("LIM-001", "Escoba de plástico", Categoria.SERVICIOS_GENERALES, Presentacion.PIEZA_INDIVIDUAL);
    }

    @Test
    @DisplayName("consultarTodos devuelve todos los materiales ordenados por nombre")
    void consultarTodos_ordenadoPorNombre() {
        assertEquals(List.of("LIM-001", "PIN-002", "PIN-001", "FER-001"), codigos(service.consultarTodos()));
    }

    @Test
    @DisplayName("consultarTodos devuelve lista vacía si no hay materiales")
    void consultarTodos_sinMateriales() {
        ConsultarMaterialesService vacio = new ConsultarMaterialesService(new FakeMaterialRepository());
        assertTrue(vacio.consultarTodos().isEmpty());
    }

    @Test
    @DisplayName("Un filtro sin criterios (o solo con texto en blanco) equivale a consultar todos")
    void consultarFiltrados_sinCriterios() {
        assertEquals(4, service.consultarFiltrados(ConsultarMaterialesQuery.sinFiltros()).size());
        assertEquals(4, service.consultarFiltrados(new ConsultarMaterialesQuery("  ", "", null, null)).size());
    }

    @Test
    @DisplayName("Filtra por parte del nombre sin distinguir mayúsculas")
    void consultarFiltrados_porNombre() {
        assertEquals(List.of("PIN-002", "PIN-001"), codigos(filtrar("PINTURA", null, null, null)));
        assertEquals(List.of("FER-001"), codigos(filtrar("hexa", null, null, null)));
    }

    @Test
    @DisplayName("Filtra por parte del código sin distinguir mayúsculas")
    void consultarFiltrados_porCodigo() {
        assertEquals(List.of("PIN-002", "PIN-001"), codigos(filtrar(null, "pin-", null, null)));
        assertEquals(List.of("FER-001"), codigos(filtrar(null, "FER-001", null, null)));
    }

    @Test
    @DisplayName("El texto del nombre no busca en el código ni al revés")
    void consultarFiltrados_nombreYCodigoSonIndependientes() {
        assertTrue(filtrar("PIN-001", null, null, null).isEmpty());
        assertTrue(filtrar(null, "pintura", null, null).isEmpty());
    }

    @Test
    @DisplayName("Filtra por categoría")
    void consultarFiltrados_porCategoria() {
        assertEquals(List.of("PIN-002", "PIN-001"), codigos(filtrar(null, null, Categoria.PINTURA, null)));
    }

    @Test
    @DisplayName("Filtra por presentación")
    void consultarFiltrados_porPresentacion() {
        assertEquals(List.of("PIN-001"), codigos(filtrar(null, null, null, Presentacion.CUBETA)));
    }

    @Test
    @DisplayName("Combina varios criterios con Y")
    void consultarFiltrados_criteriosCombinados() {
        assertEquals(List.of("PIN-002"), codigos(filtrar(null, null, Categoria.PINTURA, Presentacion.GALON)));
        assertTrue(filtrar("tornillo", null, Categoria.PINTURA, null).isEmpty());
    }

    @Test
    @DisplayName("Falla si la consulta es nula")
    void consultarFiltrados_filtroNulo() {
        assertThrows(NullPointerException.class, () -> service.consultarFiltrados(null));
    }

    private List<Material> filtrar(String nombre, String codigo, Categoria categoria, Presentacion presentacion) {
        return service.consultarFiltrados(new ConsultarMaterialesQuery(nombre, codigo, categoria, presentacion));
    }

    private void guardar(String codigo, String nombre, Categoria categoria, Presentacion presentacion) {
        repository.guardar(Material.crear(
                new CodigoMaterial(codigo),
                nombre,
                categoria,
                UnidadMedida.PIEZA,
                presentacion,
                null,
                Campus.OBREGON_NAINARI,
                BigDecimal.ONE,
                0,
                null,
                null));
    }

    private static List<String> codigos(List<Material> materiales) {
        return materiales.stream().map(m -> m.getCodigo().valor()).toList();
    }
}
