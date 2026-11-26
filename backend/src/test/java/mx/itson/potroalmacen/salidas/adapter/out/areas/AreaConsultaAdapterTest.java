package mx.itson.potroalmacen.salidas.adapter.out.areas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import mx.itson.potroalmacen.areas.application.port.in.MaterialPermitidoResumen;
import mx.itson.potroalmacen.areas.application.port.in.SolicitudMaterialArea;
import mx.itson.potroalmacen.areas.application.port.in.ValidarMaterialAreaUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AreaConsultaAdapterTest {

    @Test
    @DisplayName("Traduce los datos de Salidas a la solicitud de Áreas")
    void validarMaterialPermitido_enviaSolicitudAAreas() {
        FakeValidarMaterialArea areas = new FakeValidarMaterialArea(null);
        AreaConsultaAdapter adapter = new AreaConsultaAdapter(areas);

        adapter.validarMaterialPermitido(2, "  LIM-001 ", 4);

        assertEquals(List.of(new SolicitudMaterialArea(2, "LIM-001", 4)), areas.solicitudes);
    }

    @Test
    @DisplayName("Las excepciones de Áreas se propagan sin cambios")
    void validarMaterialPermitido_propagaExcepciones() {
        RuntimeException error = new IllegalArgumentException("material no permitido");
        AreaConsultaAdapter adapter = new AreaConsultaAdapter(new FakeValidarMaterialArea(error));

        assertSame(
                error,
                assertThrows(IllegalArgumentException.class, () -> adapter.validarMaterialPermitido(2, "LIM-001", 1)));
    }

    @Test
    @DisplayName("Exige el caso de uso de Áreas")
    void constructor_sinCasoDeUso_lanzaExcepcion() {
        assertThrows(NullPointerException.class, () -> new AreaConsultaAdapter(null));
    }

    private static final class FakeValidarMaterialArea implements ValidarMaterialAreaUseCase {
        private final RuntimeException error;
        final List<SolicitudMaterialArea> solicitudes = new ArrayList<>();

        FakeValidarMaterialArea(RuntimeException error) {
            this.error = error;
        }

        @Override
        public List<MaterialPermitidoResumen> consultarMaterialesPermitidos(Integer idArea) {
            return List.of();
        }

        @Override
        public void validar(SolicitudMaterialArea solicitud) {
            solicitudes.add(solicitud);
            if (error != null) {
                throw error;
            }
        }
    }
}
