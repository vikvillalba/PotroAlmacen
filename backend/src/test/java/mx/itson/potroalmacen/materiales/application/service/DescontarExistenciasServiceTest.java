package mx.itson.potroalmacen.materiales.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import mx.itson.potroalmacen.materiales.application.port.in.SalidaRegistrada;
import mx.itson.potroalmacen.materiales.application.port.in.SolicitudSalidaMateriales;
import mx.itson.potroalmacen.materiales.application.port.in.SolicitudSalidaMateriales.LineaSalida;
import mx.itson.potroalmacen.materiales.domain.Campus;
import mx.itson.potroalmacen.materiales.domain.Categoria;
import mx.itson.potroalmacen.materiales.domain.CodigoMaterial;
import mx.itson.potroalmacen.materiales.domain.Material;
import mx.itson.potroalmacen.materiales.domain.Presentacion;
import mx.itson.potroalmacen.materiales.domain.UnidadMedida;
import mx.itson.potroalmacen.materiales.domain.exceptions.ExistenciaInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DescontarExistenciasServiceTest {

    private FakeMaterialRepository repository;
    private DescontarExistenciasService service;

    @BeforeEach
    void setUp() {
        repository = new FakeMaterialRepository();
        service = new DescontarExistenciasService(repository);

        guardarMaterial("MAT-001", "Pintura vinílica blanca", 10);
        guardarMaterial("MAT-002", "Brocha 2 pulgadas", 5);
        guardarMaterial("MAT-003", "Thinner estándar", 8);
    }

    @Test
    @DisplayName(
            "Camino feliz: descuenta la existencia de todos los materiales solicitados y devuelve las salidas registradas")
    void descontarExistencias_caminoFeliz() {
        SolicitudSalidaMateriales solicitud =
                new SolicitudSalidaMateriales(List.of(new LineaSalida("MAT-001", 4), new LineaSalida("MAT-002", 2)));

        List<SalidaRegistrada> salidas = service.descontarExistencias(solicitud);

        assertEquals(2, salidas.size());
        assertEquals(new SalidaRegistrada("MAT-001", "Pintura vinílica blanca", 4), salidas.get(0));
        assertEquals(new SalidaRegistrada("MAT-002", "Brocha 2 pulgadas", 2), salidas.get(1));

        assertEquals(6, existencia("MAT-001"));
        assertEquals(3, existencia("MAT-002"));
        assertEquals(8, existencia("MAT-003"));
    }

    @Test
    @DisplayName(
            "Stock insuficiente de un material: lanza ExistenciaInsuficienteException y no descuenta ningún material")
    void descontarExistencias_stockInsuficienteUnMaterial() {
        SolicitudSalidaMateriales solicitud =
                new SolicitudSalidaMateriales(List.of(new LineaSalida("MAT-001", 3), new LineaSalida("MAT-002", 10)));

        ExistenciaInsuficienteException exception =
                assertThrows(ExistenciaInsuficienteException.class, () -> service.descontarExistencias(solicitud));

        assertTrue(exception.getMessage().contains("MAT-002"));
        assertEquals(10, existencia("MAT-001"));
        assertEquals(5, existencia("MAT-002"));
        assertEquals(8, existencia("MAT-003"));
    }

    @Test
    @DisplayName(
            "Stock insuficiente de varios materiales: lanza ExistenciaInsuficienteException y preserva el stock de todos")
    void descontarExistencias_stockInsuficienteVariosMateriales() {
        SolicitudSalidaMateriales solicitud =
                new SolicitudSalidaMateriales(List.of(new LineaSalida("MAT-001", 15), new LineaSalida("MAT-002", 20)));

        ExistenciaInsuficienteException exception =
                assertThrows(ExistenciaInsuficienteException.class, () -> service.descontarExistencias(solicitud));

        assertTrue(exception.getMessage().contains("MAT-001"));
        assertEquals(10, existencia("MAT-001"));
        assertEquals(5, existencia("MAT-002"));
        assertEquals(8, existencia("MAT-003"));
    }

    private void guardarMaterial(String codigo, String nombre, int cantidadInicial) {
        repository.guardar(Material.crear(
                new CodigoMaterial(codigo),
                nombre,
                Categoria.FERRETERIA,
                UnidadMedida.PIEZA,
                Presentacion.PIEZA_INDIVIDUAL,
                null,
                Campus.OBREGON_NAINARI,
                BigDecimal.TEN,
                cantidadInicial,
                0,
                null,
                null));
    }

    private int existencia(String codigo) {
        return repository
                .buscarPorCodigo(new CodigoMaterial(codigo))
                .orElseThrow()
                .getCantidad();
    }
}
