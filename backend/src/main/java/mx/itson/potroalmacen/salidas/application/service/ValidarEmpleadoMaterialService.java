package mx.itson.potroalmacen.salidas.application.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import mx.itson.potroalmacen.salidas.application.port.in.SolicitudEntregaCommand.MaterialSolicitado;
import mx.itson.potroalmacen.salidas.application.port.in.ValidarEmpleadoMaterialCommand;
import mx.itson.potroalmacen.salidas.application.port.in.ValidarEmpleadoMaterialUseCase;
import mx.itson.potroalmacen.salidas.application.port.out.BoletaRepositoryPort;
import mx.itson.potroalmacen.salidas.application.port.out.EmpleadoConsultaPort;
import mx.itson.potroalmacen.salidas.application.port.out.LimiteEntregaPort;
import mx.itson.potroalmacen.salidas.domain.LimiteEntregaMaterial;
import mx.itson.potroalmacen.salidas.domain.exceptions.BoletaInvalidaException;
import mx.itson.potroalmacen.salidas.domain.exceptions.EmpleadoNoEncontradoException;
import mx.itson.potroalmacen.salidas.domain.exceptions.LimiteEntregaExcedidoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de {@link ValidarEmpleadoMaterialUseCase}.
 *
 * <p>Valida que el solicitante exista y esté activo en el módulo Personal, y verifica que las cantidades
 * solicitadas de cada material no superen el límite de entrega configurado dentro de la ventana de días definida.
 */
@Service
@Transactional(readOnly = true)
public class ValidarEmpleadoMaterialService implements ValidarEmpleadoMaterialUseCase {

    private final EmpleadoConsultaPort empleadoConsultaPort;
    private final LimiteEntregaPort limiteEntregaPort;
    private final BoletaRepositoryPort boletaRepositoryPort;
    private final Clock clock;

    @Autowired
    public ValidarEmpleadoMaterialService(
            EmpleadoConsultaPort empleadoConsultaPort,
            LimiteEntregaPort limiteEntregaPort,
            BoletaRepositoryPort boletaRepositoryPort) {
        this(empleadoConsultaPort, limiteEntregaPort, boletaRepositoryPort, Clock.systemDefaultZone());
    }

    ValidarEmpleadoMaterialService(
            EmpleadoConsultaPort empleadoConsultaPort,
            LimiteEntregaPort limiteEntregaPort,
            BoletaRepositoryPort boletaRepositoryPort,
            Clock clock) {
        this.empleadoConsultaPort = Objects.requireNonNull(empleadoConsultaPort, "empleadoConsultaPort es obligatorio");
        this.limiteEntregaPort = Objects.requireNonNull(limiteEntregaPort, "limiteEntregaPort es obligatorio");
        this.boletaRepositoryPort = Objects.requireNonNull(boletaRepositoryPort, "boletaRepositoryPort es obligatorio");
        this.clock = Objects.requireNonNull(clock, "clock es obligatorio");
    }

    @Override
    public void validarEmpleado(ValidarEmpleadoMaterialCommand solicitud) {
        if (solicitud == null) {
            throw new BoletaInvalidaException("La solicitud no puede ser nula.");
        }
        if (solicitud.idSolicitante() == null) {
            throw new BoletaInvalidaException("El ID del solicitante es obligatorio.");
        }
        if (solicitud.materiales() == null || solicitud.materiales().isEmpty()) {
            throw new BoletaInvalidaException("Debe incluir al menos un material a validar.");
        }

        // 1. Validar que el empleado exista y esté activo (el adaptador lanza EmpleadoInactivoException si no está
        // activo)
        empleadoConsultaPort
                .buscarPorId(solicitud.idSolicitante())
                .orElseThrow(() -> new EmpleadoNoEncontradoException(solicitud.idSolicitante()));

        // 2. Validar formato de cada línea y agrupar cantidades por material
        Map<String, Integer> cantidadesPorMaterial = new LinkedHashMap<>();
        for (MaterialSolicitado linea : solicitud.materiales()) {
            validarLinea(linea);
            cantidadesPorMaterial.merge(linea.codigoMaterial(), linea.cantidadAEntregar(), Integer::sum);
        }

        // 3. Validar límite de entrega de cada material
        LocalDateTime ahora =
                (solicitud.fechaReferencia() != null) ? solicitud.fechaReferencia() : LocalDateTime.now(clock);

        for (Map.Entry<String, Integer> entry : cantidadesPorMaterial.entrySet()) {
            String codigoMaterial = entry.getKey();
            int cantidadSolicitada = entry.getValue();

            var limiteOpt = limiteEntregaPort.buscarLimite(codigoMaterial);
            if (limiteOpt.isEmpty()) {
                continue;
            }

            LimiteEntregaMaterial limite = limiteOpt.get();
            LocalDateTime desde = ahora.minusDays(limite.periodoDias());

            int entregadas = boletaRepositoryPort.cantidadEntregada(solicitud.idSolicitante(), codigoMaterial, desde);

            if (entregadas + cantidadSolicitada > limite.cantidadMaxima()) {
                throw new LimiteEntregaExcedidoException(
                        codigoMaterial, limite.cantidadMaxima(), limite.periodoDias(), entregadas, cantidadSolicitada);
            }
        }
    }

    private static void validarLinea(MaterialSolicitado linea) {
        if (linea.codigoMaterial() == null || linea.codigoMaterial().isBlank()) {
            throw new BoletaInvalidaException("El código del material es obligatorio.");
        }
        if (linea.cantidadAEntregar() == null || linea.cantidadAEntregar() <= 0) {
            throw new BoletaInvalidaException(
                    "La cantidad a entregar del material " + linea.codigoMaterial() + " debe ser mayor a cero.");
        }
    }
}
