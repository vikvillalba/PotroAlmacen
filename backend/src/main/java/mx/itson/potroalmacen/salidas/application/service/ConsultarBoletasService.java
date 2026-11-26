package mx.itson.potroalmacen.salidas.application.service;

import java.util.List;
import mx.itson.potroalmacen.salidas.application.port.in.ConsultarBoletasUseCase;
import mx.itson.potroalmacen.salidas.application.port.out.BoletaRepositoryPort;
import mx.itson.potroalmacen.salidas.domain.Boleta;
import mx.itson.potroalmacen.salidas.domain.exceptions.BoletaNoEncontradaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConsultarBoletasService implements ConsultarBoletasUseCase {

    private final BoletaRepositoryPort repo;

    public ConsultarBoletasService(BoletaRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public List<Boleta> listar() {
        return repo.listarTodas();
    }

    @Override
    public Boleta obtenerPorId(Integer id) {
        return repo.buscarPorId(id).orElseThrow(() -> new BoletaNoEncontradaException(id));
    }
}
