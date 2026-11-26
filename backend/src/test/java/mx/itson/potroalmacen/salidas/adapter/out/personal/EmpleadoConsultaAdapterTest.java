package mx.itson.potroalmacen.salidas.adapter.out.personal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import mx.itson.potroalmacen.personal.application.port.in.ConsultarEmpleadoUseCase;
import mx.itson.potroalmacen.personal.application.port.in.EmpleadoResumen;
import mx.itson.potroalmacen.personal.domain.Empleado;
import mx.itson.potroalmacen.salidas.domain.EmpleadoRef;
import mx.itson.potroalmacen.salidas.domain.exceptions.EmpleadoInactivoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmpleadoConsultaAdapterTest {

    private static final Map<Integer, EmpleadoResumen> EMPLEADOS = Map.of(
            1, new EmpleadoResumen(1, "Ana López Ruiz", "Intendente", true),
            2, new EmpleadoResumen(2, "Luis Pérez", "Almacenista", false));

    private final EmpleadoConsultaAdapter adapter = new EmpleadoConsultaAdapter(new ConsultarEmpleadoUseCase() {
        @Override
        public Optional<EmpleadoResumen> buscarPorId(Integer id) {
            return Optional.ofNullable(EMPLEADOS.get(id));
        }

        @Override
        public List<Empleado> listarTodos() {
            return List.of();
        }

        @Override
        public List<Empleado> listarAlmacen() {
            return List.of();
        }
    });

    @Test
    @DisplayName("Convierte el empleado activo de Personal a la referencia de Salidas")
    void buscarPorId_activo() {
        assertEquals(Optional.of(new EmpleadoRef(1, "Ana López Ruiz", "Intendente")), adapter.buscarPorId(1));
    }

    @Test
    @DisplayName("Un empleado inactivo se rechaza con EmpleadoInactivoException")
    void buscarPorId_inactivo() {
        assertThrows(EmpleadoInactivoException.class, () -> adapter.buscarPorId(2));
    }

    @Test
    @DisplayName("Si Personal no lo encuentra, devuelve vacío")
    void buscarPorId_inexistente() {
        assertTrue(adapter.buscarPorId(99).isEmpty());
    }
}
