package mx.itson.potroalmacen.salidas.adapter.out.materiales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import mx.itson.potroalmacen.materiales.application.port.in.ConsultarLimiteEntregaUseCase;
import mx.itson.potroalmacen.materiales.application.port.in.LimiteEntrega;
import mx.itson.potroalmacen.salidas.domain.LimiteEntregaMaterial;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LimiteEntregaAdapterTest {

    @Test
    @DisplayName("Constructor rechaza caso de uso nulo")
    void constructor_dependenciaNula_lanzaExcepcion() {
        NullPointerException error = assertThrows(NullPointerException.class, () -> new LimiteEntregaAdapter(null));
        assertEquals("consultarLimiteEntrega es obligatorio", error.getMessage());
    }

    @Test
    @DisplayName("Mapea LimiteEntrega de Materiales a LimiteEntregaMaterial de Salidas cuando existe límite")
    void buscarLimite_materialConLimite_mapeaCorrectamente() {
        ConsultarLimiteEntregaUseCase casoDeUso = codigo -> {
            if ("MAT-001".equalsIgnoreCase(codigo)) {
                return Optional.of(new LimiteEntrega("MAT-001", "Cloro", 5, 30));
            }
            return Optional.empty();
        };

        LimiteEntregaAdapter adapter = new LimiteEntregaAdapter(casoDeUso);

        Optional<LimiteEntregaMaterial> resultado = adapter.buscarLimite("MAT-001");

        assertTrue(resultado.isPresent());
        assertEquals(new LimiteEntregaMaterial(5, 30), resultado.get());
    }

    @Test
    @DisplayName("Retorna vacío cuando el material no tiene límite configurado en Materiales")
    void buscarLimite_materialSinLimite_retornaVacio() {
        ConsultarLimiteEntregaUseCase casoDeUso = codigo -> Optional.empty();

        LimiteEntregaAdapter adapter = new LimiteEntregaAdapter(casoDeUso);

        Optional<LimiteEntregaMaterial> resultado = adapter.buscarLimite("MAT-SIN-LIMITE");

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Las excepciones de Materiales se propagan directamente sin alteraciones")
    void buscarLimite_propagaExcepciones() {
        RuntimeException errorOriginal = new IllegalStateException("error simulado");
        ConsultarLimiteEntregaUseCase casoDeUso = codigo -> {
            throw errorOriginal;
        };

        LimiteEntregaAdapter adapter = new LimiteEntregaAdapter(casoDeUso);

        RuntimeException errorLanzado =
                assertThrows(IllegalStateException.class, () -> adapter.buscarLimite("MAT-001"));
        assertSame(errorOriginal, errorLanzado);
    }
}
