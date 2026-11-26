package mx.itson.potroalmacen.personal.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import mx.itson.potroalmacen.personal.application.port.out.EmpleadoRepositoryConcurrenciaPort;
import mx.itson.potroalmacen.personal.application.port.out.EmpleadoRepositoryPort;
import mx.itson.potroalmacen.personal.domain.Empleado;
import org.springframework.stereotype.Repository;

@Repository
public class EmpleadoPersistenceAdapter implements EmpleadoRepositoryPort, EmpleadoRepositoryConcurrenciaPort {

    private final SpringDataEmpleadoRepository repository;
    private final EmpleadoMapper mapper;

    public EmpleadoPersistenceAdapter(SpringDataEmpleadoRepository repository, EmpleadoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Empleado> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id.longValue()).map(mapper::toDomain);
    }

    @Override
    public Optional<Empleado> buscarPorIdConBloqueo(Integer id) {
        return repository.findByIdConBloqueo(id.longValue()).map(mapper::toDomain);
    }

    @Override
    public List<Empleado> listarTodos() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}
