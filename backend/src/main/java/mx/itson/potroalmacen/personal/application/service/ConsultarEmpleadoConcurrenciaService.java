package mx.itson.potroalmacen.personal.application.service;

import java.util.Objects;
import java.util.Optional;
import mx.itson.potroalmacen.personal.application.port.in.ConsultarEmpleadoConcurrenciaUseCase;
import mx.itson.potroalmacen.personal.application.port.in.EmpleadoResumen;
import mx.itson.potroalmacen.personal.application.port.out.EmpleadoRepositoryConcurrenciaPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConsultarEmpleadoConcurrenciaService implements ConsultarEmpleadoConcurrenciaUseCase {

    private final EmpleadoRepositoryConcurrenciaPort repositorioBloqueo;

    @Autowired
    public ConsultarEmpleadoConcurrenciaService(EmpleadoRepositoryConcurrenciaPort repositorioBloqueo) {
        this.repositorioBloqueo = Objects.requireNonNull(repositorioBloqueo, "repositorioBloqueo es obligatorio");
    }

    @Override
    public Optional<EmpleadoResumen> buscarPorIdConBloqueo(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return repositorioBloqueo.buscarPorIdConBloqueo(id).map(ConsultarEmpleadoService::aResumen);
    }
}
