package mx.itson.potroalmacen.materiales.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarMaterialCommand;
import mx.itson.potroalmacen.materiales.application.port.out.MaterialRepositoryPort;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Departamento;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;
import mx.itson.potroalmacen.materiales.domain.exceptions.CodigoDuplicadoException;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegistrarMaterialServiceTest {

    private FakeMaterialRepository repository;
    private RegistrarMaterialService service;

    @BeforeEach
    void setUp() {
        repository = new FakeMaterialRepository();
        service = new RegistrarMaterialService(repository);
    }

    @Test
    @DisplayName("Debe registrar exitosamente un material y asignar la existencia inicial")
    void registrar_exitosoConExistenciaInicial() {
        RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                "MAT-001",
                "Pintura Vinílica Blanca",
                Categoria.PINTURA,
                UnidadMedida.LITRO,
                Presentacion.CUBETA,
                "19 L",
                Campus.OBREGON_NAINARI,
                new BigDecimal("450.00"),
                15,
                5,
                50,
                "Mantener en lugar fresco");

        Material registrado = service.registrar(comando);

        assertNotNull(registrado);
        assertNotNull(registrado.getId());
        assertEquals("MAT-001", registrado.getCodigo().toString());
        assertEquals("Pintura Vinílica Blanca", registrado.getNombre());
        assertEquals(Campus.OBREGON_NAINARI, registrado.getCampus());
        assertEquals(Categoria.PINTURA, registrado.getCategoria());
        assertEquals(Departamento.MANTENIMIENTO, registrado.getDepartamento());
        assertEquals(Presentacion.CUBETA, registrado.getPresentacion());
        assertEquals("19 L", registrado.getContenidoPresentacion());
        assertEquals(15, registrado.getCantidad());
        assertEquals(5, registrado.getStockMinimo());
        assertEquals(50, registrado.getStockMaximo());
        assertEquals("Mantener en lugar fresco", registrado.getUsoPrecauciones());

        // Verificar que quedó en el repositorio
        assertTrue(repository.existePorCodigo(new CodigoMaterial("MAT-001")));
    }

    @Test
    @DisplayName("Debe registrar exitosamente un material con existencia inicial cero")
    void registrar_exitosoConExistenciaInicialCero() {
        RegistrarMaterialCommand comando = new RegistrarMaterialCommand(
                "MAT-002",
                "Tornillo hexagonal 1/2 pulgada",
                Categoria.FERRETERIA,
                UnidadMedida.PIEZA,
                Presentacion.CAJA,
                "100 piezas",
                Campus.OBREGON_CENTRO,
                new BigDecimal("2.50"),
                0,
                20,
                null,
                null);

        Material registrado = service.registrar(comando);

        assertNotNull(registrado);
        assertEquals(0, registrado.getCantidad());
        assertEquals(20, registrado.getStockMinimo());
        assertNull(registrado.getStockMaximo());
        assertNull(registrado.getUsoPrecauciones());
    }

    @Test
    @DisplayName("Debe lanzar CodigoDuplicadoException si el código ya existe en el sistema")
    void registrar_codigoDuplicado_lanzaExcepcion() {
        RegistrarMaterialCommand comando1 = new RegistrarMaterialCommand(
                "DUP-001",
                "Material Original",
                Categoria.FERRETERIA,
                UnidadMedida.PIEZA,
                Presentacion.PIEZA_INDIVIDUAL,
                null,
                Campus.GUAYMAS,
                BigDecimal.TEN,
                5,
                2,
                null,
                null);
        service.registrar(comando1);

        RegistrarMaterialCommand comandoDuplicado = new RegistrarMaterialCommand(
                "DUP-001",
                "Material Duplicado",
                Categoria.FERRETERIA,
                UnidadMedida.PIEZA,
                Presentacion.PIEZA_INDIVIDUAL,
                null,
                Campus.GUAYMAS,
                BigDecimal.ONE,
                10,
                1,
                null,
                null);

        CodigoDuplicadoException excepcion =
                assertThrows(CodigoDuplicadoException.class, () -> service.registrar(comandoDuplicado));

        assertEquals("DUP-001", excepcion.getCodigo());
    }

    @Test
    @DisplayName("Debe fallar si la existencia inicial es negativa")
    void registrar_existenciaInicialNegativa_lanzaExcepcion() {
        assertThrows(
                MaterialInvalidoException.class,
                () -> new RegistrarMaterialCommand(
                        "MAT-003",
                        "Material Test",
                        Categoria.FERRETERIA,
                        UnidadMedida.PIEZA,
                        Presentacion.PIEZA_INDIVIDUAL,
                        null,
                        Campus.EMPALME,
                        BigDecimal.ONE,
                        -5,
                        0,
                        null,
                        null));
    }

    @Test
    @DisplayName("Debe fallar si los campos obligatorios del dominio están vacíos")
    void registrar_camposObligatoriosVacios_lanzaExcepcion() {
        RegistrarMaterialCommand comandoSinNombre = new RegistrarMaterialCommand(
                "MAT-004",
                "   ",
                Categoria.FERRETERIA,
                UnidadMedida.PIEZA,
                Presentacion.PIEZA_INDIVIDUAL,
                null,
                Campus.NAVOJOA_SUR, BigDecimal.ONE, 0, 0, null, null);

        assertThrows(MaterialInvalidoException.class, () -> service.registrar(comandoSinNombre));
    }

    @Test
    @DisplayName("Debe fallar si falta la categoría")
    void registrar_sinCategoria_lanzaExcepcion() {
        RegistrarMaterialCommand comandoSinCategoria = new RegistrarMaterialCommand(
                "MAT-005",
                "Material sin categoría",
                null,
                UnidadMedida.PIEZA,
                Presentacion.PIEZA_INDIVIDUAL,
                null,
                Campus.GUAYMAS,
                BigDecimal.ONE,
                0,
                0,
                null,
                null);

        assertThrows(MaterialInvalidoException.class, () -> service.registrar(comandoSinCategoria));
    }

    /**
     * Repositorio falso en memoria para pruebas unitarias sin Spring ni base de datos.
     */
    private static class FakeMaterialRepository implements MaterialRepositoryPort {
        private final Map<String, Material> storage = new HashMap<>();
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
    }
}
