package mx.itson.potroalmacen.salidas.adapter.out.materiales;

import java.util.Objects;
import java.util.Optional;
import mx.itson.potroalmacen.materiales.application.port.in.ConsultarLimiteEntregaUseCase;
import mx.itson.potroalmacen.salidas.application.port.out.LimiteEntregaPort;
import mx.itson.potroalmacen.salidas.domain.LimiteEntregaMaterial;
import org.springframework.stereotype.Component;

/** Implementa {@link LimiteEntregaPort} consultando el límite en el módulo Materiales. */
@Component
public class LimiteEntregaAdapter implements LimiteEntregaPort {

    private final ConsultarLimiteEntregaUseCase consultarLimiteEntrega;

    public LimiteEntregaAdapter(ConsultarLimiteEntregaUseCase consultarLimiteEntrega) {
        this.consultarLimiteEntrega =
                Objects.requireNonNull(consultarLimiteEntrega, "consultarLimiteEntrega es obligatorio");
    }

    @Override
    public Optional<LimiteEntregaMaterial> buscarLimite(String codigoMaterial) {
        return consultarLimiteEntrega
                .consultarLimiteEntrega(codigoMaterial)
                .map(limite -> new LimiteEntregaMaterial(limite.cantidadMaxima(), limite.periodoDias()));
    }
}
