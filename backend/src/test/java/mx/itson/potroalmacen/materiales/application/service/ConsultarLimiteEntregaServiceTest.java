package mx.itson.potroalmacen.materiales.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.application.port.in.LimiteEntrega;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConsultarLimiteEntregaServiceTest {

    private FakeMaterialRepository repository;
    private ConsultarLimiteEntregaService service;

    @BeforeEach
    void setUp() {
        repository = new FakeMaterialRepository();
        service = new ConsultarLimiteEntregaService(repository);

        guardarMaterial("MAT-CON-LIMITE", "Cloro Industrial 1L", 5, 30);
        guardarMaterial("MAT-SIN-LIMITE", "Franela de microfibra", null, null);
    }

    @Test
    @DisplayName("Constructor rechaza repositorio nulo")
    void constructor_repositorioNulo_lanzaExcepcion() {
        NullPointerException error =
                assertThrows(NullPointerException.class, () -> new ConsultarLimiteEntregaService(null));
        assertEquals("repositorio es obligatorio", error.getMessage());
    }

    @Test
    @DisplayName("Retorna el límite configurado cuando el material tiene límite de entrega")
    void consultarLimiteEntrega_materialConLimite_retornaLimite() {
        Optional<LimiteEntrega> resultado = service.consultarLimiteEntrega("MAT-CON-LIMITE");

        assertTrue(resultado.isPresent());
        LimiteEntrega limite = resultado.get();
        assertEquals("MAT-CON-LIMITE", limite.codigoMaterial());
        assertEquals("Cloro Industrial 1L", limite.nombreMaterial());
        assertEquals(5, limite.cantidadMaxima());
        assertEquals(30, limite.periodoDias());
    }

    @Test
    @DisplayName("La búsqueda no distingue mayúsculas ni minúsculas en el código")
    void consultarLimiteEntrega_codigoMinusculas_retornaLimite() {
        Optional<LimiteEntrega> resultado = service.consultarLimiteEntrega("mat-con-limite");

        assertTrue(resultado.isPresent());
        assertEquals("MAT-CON-LIMITE", resultado.get().codigoMaterial());
    }

    @Test
    @DisplayName("Retorna vacío cuando el material existe pero no tiene límite de entrega configurado")
    void consultarLimiteEntrega_materialSinLimite_retornaVacio() {
        Optional<LimiteEntrega> resultado = service.consultarLimiteEntrega("MAT-SIN-LIMITE");

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Lanza MaterialNoEncontradoException cuando el material no existe en el repositorio")
    void consultarLimiteEntrega_materialInexistente_lanzaExcepcion() {
        MaterialNoEncontradoException error = assertThrows(
                MaterialNoEncontradoException.class, () -> service.consultarLimiteEntrega("MAT-NO-EXISTE"));

        assertTrue(error.getMessage().contains("MAT-NO-EXISTE"));
    }

    @Test
    @DisplayName("Lanza MaterialInvalidoException cuando el código es nulo o en blanco")
    void consultarLimiteEntrega_codigoInvalido_lanzaExcepcion() {
        assertThrows(MaterialInvalidoException.class, () -> service.consultarLimiteEntrega(null));
        assertThrows(MaterialInvalidoException.class, () -> service.consultarLimiteEntrega("   "));
    }

    private void guardarMaterial(String codigo, String nombre, Integer limitePorEmpleado, Integer periodoDias) {
        repository.guardar(Material.crear(
                new CodigoMaterial(codigo),
                nombre,
                Categoria.SERVICIOS_GENERALES,
                UnidadMedida.PIEZA,
                Presentacion.PIEZA_INDIVIDUAL,
                "1 pieza",
                Campus.OBREGON_NAINARI,
                new BigDecimal("25.00"),
                20,
                5,
                50,
                null,
                limitePorEmpleado,
                periodoDias));
    }
}
