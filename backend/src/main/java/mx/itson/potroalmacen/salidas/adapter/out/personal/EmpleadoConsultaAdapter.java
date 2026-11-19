package mx.itson.potroalmacen.salidas.adapter.out.personal;

import java.util.Objects;
import java.util.Optional;
import mx.itson.potroalmacen.personal.application.port.in.ConsultarEmpleadoUseCase;
import mx.itson.potroalmacen.personal.application.port.in.EmpleadoResumen;
import mx.itson.potroalmacen.salidas.application.port.out.EmpleadoConsultaPort;
import mx.itson.potroalmacen.salidas.domain.EmpleadoRef;
import mx.itson.potroalmacen.salidas.domain.exceptions.EmpleadoInactivoException;
import org.springframework.stereotype.Component;

/**
 * Implementa {@link EmpleadoConsultaPort} consultando al módulo Personal.
 *
 * <p>Los empleados inactivos no pueden participar en una entrega: se rechazan con {@link EmpleadoInactivoException}.
 */
@Component
public class EmpleadoConsultaAdapter implements EmpleadoConsultaPort {

    private final ConsultarEmpleadoUseCase consultarEmpleado;

    public EmpleadoConsultaAdapter(ConsultarEmpleadoUseCase consultarEmpleado) {
        this.consultarEmpleado = Objects.requireNonNull(consultarEmpleado, "consultarEmpleado es obligatorio");
    }

    @Override
    public Optional<EmpleadoRef> buscarPorId(Integer idEmpleado) {
        return consultarEmpleado.buscarPorId(idEmpleado).map(EmpleadoConsultaAdapter::aReferencia);
    }

    private static EmpleadoRef aReferencia(EmpleadoResumen empleado) {
        if (!empleado.activo()) {
            throw new EmpleadoInactivoException(empleado.id());
        }
        return new EmpleadoRef(empleado.id(), empleado.nombreCompleto(), empleado.puesto());
    }
}
