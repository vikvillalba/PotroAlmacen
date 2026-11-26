package mx.itson.potroalmacen.personal.application.service;

import java.util.Objects;
import java.util.Optional;
import mx.itson.potroalmacen.personal.application.port.in.ConsultarEmpleadoUseCase;
import mx.itson.potroalmacen.personal.application.port.in.EmpleadoResumen;
import mx.itson.potroalmacen.personal.application.port.out.EmpleadoRepositoryPort;
import mx.itson.potroalmacen.personal.domain.Empleado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementación de {@link ConsultarEmpleadoUseCase}. Solo lectura. */
@Service
@Transactional(readOnly = true)
public class ConsultarEmpleadoService implements ConsultarEmpleadoUseCase {

    private final EmpleadoRepositoryPort repositorio;

    public ConsultarEmpleadoService(EmpleadoRepositoryPort repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio es obligatorio");
    }

    @Override
    public Optional<EmpleadoResumen> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return repositorio.buscarPorId(id).map(ConsultarEmpleadoService::aResumen);
    }

    static EmpleadoResumen aResumen(Empleado empleado) {
        return new EmpleadoResumen(
                empleado.getId(), empleado.getNombreCompleto(), empleado.getPuesto(), empleado.isActivo());
    }
}
