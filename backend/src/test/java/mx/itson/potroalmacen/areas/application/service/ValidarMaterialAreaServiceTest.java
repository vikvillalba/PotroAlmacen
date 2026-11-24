package mx.itson.potroalmacen.areas.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mx.itson.potroalmacen.areas.application.port.in.MaterialPermitidoResumen;
import mx.itson.potroalmacen.areas.application.port.in.SolicitudMaterialArea;
import mx.itson.potroalmacen.areas.application.port.out.AreaRepositoryPort;
import mx.itson.potroalmacen.areas.domain.Area;
import mx.itson.potroalmacen.areas.domain.Espacio;
import mx.itson.potroalmacen.areas.domain.FrecuenciaLimpieza;
import mx.itson.potroalmacen.areas.domain.MaterialRef;
import mx.itson.potroalmacen.areas.domain.TipoArea;
import mx.itson.potroalmacen.areas.domain.exceptions.AreaNoEncontradaException;
import mx.itson.potroalmacen.areas.domain.exceptions.CantidadExcedeAsignacionAreaException;
import mx.itson.potroalmacen.areas.domain.exceptions.CantidadMaterialInvalidaException;
import mx.itson.potroalmacen.areas.domain.exceptions.MaterialNoPermitidoEnAreaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValidarMaterialAreaServiceTest {

    private static final int ID_AREA = 1;

    private FakeAreaRepository areas;
    private ValidarMaterialAreaService service;

    @BeforeEach
    void setUp() {
        areas = new FakeAreaRepository();
        areas.agregar(
                ID_AREA,
                Area.crear(
                        "Edificio A",
                        TipoArea.values()[0],
                        new BigDecimal("120.50"),
                        List.of(new Espacio("Aula 101", null, FrecuenciaLimpieza.values()[0])),
                        List.of(
                                new MaterialRef("MAT-001", "Escoba", 10),
                                new MaterialRef("MAT-002", "Jabón", 5))));
        service = new ValidarMaterialAreaService(areas);
    }

    @Test
    @DisplayName("Pasa cuando el material está catalogado y la cantidad no supera lo asignado")
    void validar_materialPermitidoYCantidadValida_exitoso() {
        assertDoesNotThrow(() -> service.validar(new SolicitudMaterialArea(ID_AREA, "MAT-001", 4)));
    }

    @Test
    @DisplayName("Pasa cuando la cantidad es exactamente la asignada")
    void validar_cantidadIgualALaAsignada_exitoso() {
        assertDoesNotThrow(() -> service.validar(new SolicitudMaterialArea(ID_AREA, "MAT-001", 10)));
    }

    @Test
    @DisplayName("Lanza CantidadExcedeAsignacionAreaException cuando supera la cantidad asignada")
    void validar_cantidadMayorALaAsignada_lanzaExcepcion() {
        var ex = assertThrows(
                CantidadExcedeAsignacionAreaException.class,
                () -> service.validar(new SolicitudMaterialArea(ID_AREA, "MAT-001", 11)));

        assertEquals("MAT-001", ex.getCodigoMaterial());
        assertEquals(10, ex.getCantidadAsignada());
        assertEquals(11, ex.getCantidadSolicitada());
    }

    @Test
    @DisplayName("Lanza MaterialNoPermitidoEnAreaException cuando el material no está catalogado en el área")
    void validar_materialNoCatalogado_lanzaExcepcion() {
        var ex = assertThrows(
                MaterialNoPermitidoEnAreaException.class,
                () -> service.validar(new SolicitudMaterialArea(ID_AREA, "MAT-999", 1)));

        assertEquals("MAT-999", ex.getCodigoMaterial());
    }

    @Test
    @DisplayName("Lanza CantidadMaterialInvalidaException cuando la cantidad es nula, cero o negativa")
    void validar_cantidadNoPositiva_lanzaExcepcion() {
        assertThrows(
                CantidadMaterialInvalidaException.class,
                () -> service.validar(new SolicitudMaterialArea(ID_AREA, "MAT-001", null)));
        assertThrows(
                CantidadMaterialInvalidaException.class,
                () -> service.validar(new SolicitudMaterialArea(ID_AREA, "MAT-001", 0)));
        assertThrows(
                CantidadMaterialInvalidaException.class,
                () -> service.validar(new SolicitudMaterialArea(ID_AREA, "MAT-001", -3)));
    }

    @Test
    @DisplayName("Lanza AreaNoEncontradaException cuando el área no existe")
    void validar_areaInexistente_lanzaExcepcion() {
        assertThrows(
                AreaNoEncontradaException.class,
                () -> service.validar(new SolicitudMaterialArea(99, "MAT-001", 1)));
    }

    @Test
    @DisplayName("Lanza IllegalArgumentException cuando falta el área, el código o la solicitud")
    void validar_datosObligatoriosFaltantes_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> service.validar(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> service.validar(new SolicitudMaterialArea(null, "MAT-001", 1)));
        assertThrows(
                IllegalArgumentException.class, () -> service.validar(new SolicitudMaterialArea(ID_AREA, "  ", 1)));
    }

    @Test
    @DisplayName("Lista los materiales permitidos del área")
    void consultarMaterialesPermitidos_devuelveCatalogoDelArea() {
        List<MaterialPermitidoResumen> resultado = service.consultarMaterialesPermitidos(ID_AREA);

        assertEquals(
                List.of(new MaterialPermitidoResumen("MAT-001", "Escoba"), new MaterialPermitidoResumen("MAT-002", "Jabón")),
                resultado);
    }

    @Test
    @DisplayName("Consultar materiales de un área inexistente lanza AreaNoEncontradaException")
    void consultarMaterialesPermitidos_areaInexistente_lanzaExcepcion() {
        assertThrows(AreaNoEncontradaException.class, () -> service.consultarMaterialesPermitidos(99));
    }

    private static final class FakeAreaRepository implements AreaRepositoryPort {
        private final Map<Integer, Area> almacen = new HashMap<>();

        void agregar(int id, Area area) {
            almacen.put(id, area.conId(id));
        }

        @Override
        public Optional<Area> buscarPorId(Integer id) {
            return Optional.ofNullable(almacen.get(id));
        }

        @Override
        public List<Area> listarTodas() {
            return List.copyOf(almacen.values());
        }

        @Override
        public Area guardar(Area area) {
            throw new UnsupportedOperationException();
        }
    }
}