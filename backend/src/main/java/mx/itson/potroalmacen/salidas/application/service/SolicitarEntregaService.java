package mx.itson.potroalmacen.salidas.application.service;

import java.util.List;
import java.util.Objects;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitarEntregaUseCase;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand.MaterialSolicitado;
import mx.itson.potroalmacen.salidas.application.port.in.ValidarEmpleadoMaterialCommand;
import mx.itson.potroalmacen.salidas.application.port.in.ValidarEmpleadoMaterialUseCase;
import mx.itson.potroalmacen.salidas.application.port.out.BoletaRepositoryPort;
import mx.itson.potroalmacen.salidas.application.port.out.EmpleadoConsultaConcurrenciaPort;
import mx.itson.potroalmacen.salidas.application.port.out.InventarioPort;
import mx.itson.potroalmacen.salidas.domain.Boleta;
import mx.itson.potroalmacen.salidas.domain.EmpleadoRef;
import mx.itson.potroalmacen.salidas.domain.MaterialRef;
import mx.itson.potroalmacen.salidas.domain.exceptions.BoletaInvalidaException;
import mx.itson.potroalmacen.salidas.domain.exceptions.EmpleadoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del caso de uso SolicitarEntregaUseCase.
 * <p>
 * Obtiene al solicitante y al entregador, valida el estado del empleado y los límites de entrega configurados,
 * registra en el inventario la salida de cada material (Materiales verifica la existencia), crea la boleta con
 * las reglas del dominio y la guarda. Ocurre en una transacción: si algo falla, las salidas ya registradas
 * se revierten.
 */
@Service
@Transactional
public class SolicitarEntregaService implements SolicitarEntregaUseCase {

    private final EmpleadoConsultaConcurrenciaPort empleados;
    private final InventarioPort inventario;
    private final BoletaRepositoryPort boletaRepository;
    private final ValidarEmpleadoMaterialUseCase validarEmpleado;

    public SolicitarEntregaService(
            EmpleadoConsultaConcurrenciaPort empleados,
            InventarioPort inventario,
            BoletaRepositoryPort boletaRepository,
            ValidarEmpleadoMaterialUseCase validarEmpleado) {
        this.empleados = Objects.requireNonNull(empleados, "empleados es obligatorio");
        this.inventario = Objects.requireNonNull(inventario, "inventario es obligatorio");
        this.boletaRepository = Objects.requireNonNull(boletaRepository, "boletaRepository es obligatorio");
        this.validarEmpleado = Objects.requireNonNull(validarEmpleado, "validarEmpleado es obligatorio");
    }

    @Override
    public Boleta solicitar(SolicitudEntregaCommand comando) {
        Objects.requireNonNull(comando, "El comando no puede ser nulo");

        EmpleadoRef solicitante = buscarEmpleado(comando.idSolicitante());
        EmpleadoRef entregador = buscarEmpleado(comando.idEntregador());

        comando.materiales().forEach(SolicitarEntregaService::validarLinea);

        if (comando.idSolicitante() != null && !comando.materiales().isEmpty()) {
            validarEmpleado.validarEmpleado(
                    new ValidarEmpleadoMaterialCommand(comando.idSolicitante(), comando.materiales()));
        }

        List<MaterialRef> materiales = comando.materiales().stream()
                .map(linea -> inventario.registrarSalida(linea.codigoMaterial(), linea.cantidadAEntregar()))
                .toList();

        Boleta boleta = Boleta.crear(solicitante, entregador, materiales);
        return boletaRepository.guardar(boleta);
    }

    /** Un id nulo se deja pasar como null para que el dominio responda con su regla de campo obligatorio. */
    private EmpleadoRef buscarEmpleado(Integer idEmpleado) {
        if (idEmpleado == null) {
            return null;
        }
        return empleados
                .buscarPorIdConBloqueo(idEmpleado)
                .orElseThrow(() -> new EmpleadoNoEncontradoException(idEmpleado));
    }

    /** Se valida antes de tocar el inventario para no registrar salidas de una solicitud mal formada. */
    private static void validarLinea(MaterialSolicitado linea) {
        if (linea.codigoMaterial() == null) {
            throw new BoletaInvalidaException("El código del material es obligatorio.");
        }
        if (linea.cantidadAEntregar() == null || linea.cantidadAEntregar() <= 0) {
            throw new BoletaInvalidaException(
                    "La cantidad a entregar del material " + linea.codigoMaterial() + " debe ser mayor a cero.");
        }
    }
}
