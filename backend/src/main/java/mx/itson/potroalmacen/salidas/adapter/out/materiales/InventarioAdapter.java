package mx.itson.potroalmacen.salidas.adapter.out.materiales;

import java.util.Objects;
import mx.itson.potroalmacen.materiales.application.port.in.RegistrarSalidaMaterialUseCase;
import mx.itson.potroalmacen.materiales.application.port.in.SalidaRegistrada;
import mx.itson.potroalmacen.salidas.application.port.out.InventarioPort;
import mx.itson.potroalmacen.salidas.domain.MaterialRef;
import org.springframework.stereotype.Component;

/** Implementa {@link InventarioPort} registrando la salida en el módulo Materiales. */
@Component
public class InventarioAdapter implements InventarioPort {

    private final RegistrarSalidaMaterialUseCase registrarSalidaMaterial;

    public InventarioAdapter(RegistrarSalidaMaterialUseCase registrarSalidaMaterial) {
        this.registrarSalidaMaterial =
                Objects.requireNonNull(registrarSalidaMaterial, "registrarSalidaMaterial es obligatorio");
    }

    @Override
    public MaterialRef registrarSalida(String codigoMaterial, int cantidad) {
        SalidaRegistrada salida = registrarSalidaMaterial.registrarSalida(codigoMaterial, cantidad);
        return new MaterialRef(salida.codigoMaterial(), salida.nombreMaterial(), salida.cantidad());
    }
}
