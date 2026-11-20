package mx.itson.potroalmacen.materiales.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import mx.itson.potroalmacen.materiales.application.port.in.SalidaRegistrada;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;
import mx.itson.potroalmacen.materiales.domain.exceptions.ExistenciaInsuficienteException;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegistrarSalidaMaterialServiceTest {

    private FakeMaterialRepository repository;
    private RegistrarSalidaMaterialService service;

    @BeforeEach
    void setUp() {
        repository = new FakeMaterialRepository();
        service = new RegistrarSalidaMaterialService(repository);
        repository.guardar(Material.crear(
                new CodigoMaterial("LIM-001"),
                "Cloro",
                Categoria.FERRETERIA,
                UnidadMedida.PIEZA,
                Presentacion.PIEZA_INDIVIDUAL,
                null,
                Campus.OBREGON_NAINARI,
                BigDecimal.ONE,
                10,
                0,
                null,
                null));
    }

    @Test
    @DisplayName("Descuenta la existencia y devuelve código, nombre y cantidad")
    void registrarSalida_valida() {
        SalidaRegistrada salida = service.registrarSalida(" lim-001 ", 4);

        assertEquals(new SalidaRegistrada("LIM-001", "Cloro", 4), salida);
        assertEquals(6, existencia("LIM-001"));
    }

    @Test
    @DisplayName("Un código inexistente lanza MaterialNoEncontradoException")
    void registrarSalida_inexistente() {
        assertThrows(MaterialNoEncontradoException.class, () -> service.registrarSalida("NO-EXISTE", 1));
    }

    @Test
    @DisplayName("Más de la existencia lanza ExistenciaInsuficienteException y no descuenta")
    void registrarSalida_existenciaInsuficiente() {
        assertThrows(ExistenciaInsuficienteException.class, () -> service.registrarSalida("LIM-001", 11));
        assertEquals(10, existencia("LIM-001"));
    }

    @Test
    @DisplayName("Una cantidad no positiva la rechaza el dominio")
    void registrarSalida_cantidadInvalida() {
        assertThrows(MaterialInvalidoException.class, () -> service.registrarSalida("LIM-001", 0));
        assertEquals(10, existencia("LIM-001"));
    }

    private int existencia(String codigo) {
        return repository
                .buscarPorCodigo(new CodigoMaterial(codigo))
                .orElseThrow()
                .getCantidad();
    }
}
