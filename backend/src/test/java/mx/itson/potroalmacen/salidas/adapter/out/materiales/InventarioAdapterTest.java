package mx.itson.potroalmacen.salidas.adapter.out.materiales;

import static org.junit.jupiter.api.Assertions.*;

import mx.itson.potroalmacen.materiales.application.port.in.SalidaRegistrada;
import mx.itson.potroalmacen.salidas.domain.MaterialRef;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InventarioAdapterTest {

    @Test
    @DisplayName("Convierte la salida registrada por Materiales a la referencia de Salidas")
    void registrarSalida_conviertePorReferencia() {
        InventarioAdapter adapter = new InventarioAdapter(
                (codigo, cantidad) -> new SalidaRegistrada(codigo.trim().toUpperCase(), "Cloro", cantidad));

        assertEquals(new MaterialRef("LIM-001", "Cloro", 3), adapter.registrarSalida("lim-001", 3));
    }

    @Test
    @DisplayName("Las excepciones de Materiales se propagan sin cambios")
    void registrarSalida_propagaExcepciones() {
        RuntimeException error = new IllegalStateException("sin existencia");
        InventarioAdapter adapter = new InventarioAdapter((codigo, cantidad) -> {
            throw error;
        });

        assertSame(error, assertThrows(IllegalStateException.class, () -> adapter.registrarSalida("LIM-001", 1)));
    }
}
