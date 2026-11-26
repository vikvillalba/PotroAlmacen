package mx.itson.potroalmacen.personal.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import mx.itson.potroalmacen.personal.application.port.in.EmpleadoResumen;
import mx.itson.potroalmacen.personal.application.port.out.EmpleadoRepositoryPort;
import mx.itson.potroalmacen.personal.domain.Empleado;
import mx.itson.potroalmacen.personal.domain.TipoEmpleado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConsultarEmpleadoServiceTest {

    private static final Empleado ANA = Empleado.crear(
                    "Ana", "López", "Ruiz", TipoEmpleado.ITSON, "Intendente", null, null, List.of())
            .reconstitadir(1, true, false);
    private static final Empleado LUIS = Empleado.crear(
                    "Luis", "Pérez", null, TipoEmpleado.EXTERNO, "Almacenista", null, null, List.of())
            .reconstitadir(2, false, false);
    private static final Empleado CARLOS = Empleado.crear(
                    "Carlos", "Soto", "Mendoza", TipoEmpleado.ITSON, "Encargado de Almacén", null, null, List.of())
            .reconstitadir(3, true, false);

    private final ConsultarEmpleadoService service = new ConsultarEmpleadoService(new EmpleadoRepositoryPort() {
        private final Map<Integer, Empleado> datos = Map.of(1, ANA, 2, LUIS, 3, CARLOS);

        @Override
        public Optional<Empleado> buscarPorId(Integer id) {
            return id == null ? Optional.empty() : Optional.ofNullable(datos.get(id));
        }

        @Override
        public List<Empleado> listarTodos() {
            return List.of(ANA, LUIS, CARLOS);
        }
    });

    @Test
    @DisplayName("Devuelve el resumen del empleado con su nombre completo, puesto y estado")
    void buscarPorId_existente() {
        assertEquals(Optional.of(new EmpleadoResumen(1, "Ana López Ruiz", "Intendente", true)), service.buscarPorId(1));
        assertEquals(Optional.of(new EmpleadoResumen(2, "Luis Pérez", "Almacenista", false)), service.buscarPorId(2));
    }

    @Test
    @DisplayName("Un id inexistente o nulo devuelve vacío")
    void buscarPorId_inexistente() {
        assertTrue(service.buscarPorId(99).isEmpty());
        assertTrue(service.buscarPorId(null).isEmpty());
    }

    @Test
    @DisplayName("Lista todo el personal y filtra solo los activos de almacén")
    void listarTodos_y_listarAlmacen() {
        assertEquals(3, service.listarTodos().size());
        assertEquals(List.of(CARLOS), service.listarAlmacen());
    }
}
