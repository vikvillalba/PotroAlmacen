package mx.itson.potroalmacen.salidas.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import mx.itson.potroalmacen.salidas.domain.exceptions.BoletaInvalidaException;

public class Boleta {

    private final Integer id;
    private final String folio;
    private final EmpleadoRef solicitante;
    private final EmpleadoRef entregador;
    private final LocalDateTime fechaHora;
    private EstadoBoleta estado;
    private final List<MaterialRef> materiales;

    // private String firma; // Firma omitida por ahora

    private Boleta(
            Integer id,
            String folio,
            EmpleadoRef solicitante,
            EmpleadoRef entregador,
            LocalDateTime fechaHora,
            EstadoBoleta estado,
            List<MaterialRef> materiales) {
        this.id = id;
        this.folio = folio != null ? folio : generarFolioUnico();
        this.solicitante = solicitante;
        this.entregador = entregador;
        this.fechaHora = fechaHora != null ? fechaHora : LocalDateTime.now();
        this.estado = estado != null ? estado : EstadoBoleta.CREADA;
        this.materiales = materiales != null ? new ArrayList<>(materiales) : new ArrayList<>();
    }

    /** Fábrica de dominio para registrar y completar una nueva boleta de entrega. */
    public static Boleta crear(EmpleadoRef solicitante, EmpleadoRef entregador, List<MaterialRef> materiales) {
        validarDatos(solicitante, entregador, materiales);
        return new Boleta(
                null,
                null, // Se genera automáticamente el folio
                solicitante,
                entregador,
                LocalDateTime.now(),
                EstadoBoleta.COMPLETADA,
                materiales);
    }

    /** Reconstituye el agregado con su ID y folio desde la base de datos. */
    public Boleta conIdYFolio(Integer id, String folio) {
        return new Boleta(id, folio, this.solicitante, this.entregador, this.fechaHora, this.estado, this.materiales);
    }

    // MÉTODOS DE NEGOCIO / COMPORTAMIENTO

    public void completarEntrega() {
        if (this.estado == EstadoBoleta.COMPLETADA) {
            throw new BoletaInvalidaException("La boleta ya se encuentra completada.");
        }
        this.estado = EstadoBoleta.COMPLETADA;
    }

    public void cancelar() {
        this.estado = EstadoBoleta.CANCELADA;
    }

    private static void validarDatos(EmpleadoRef solicitante, EmpleadoRef entregador, List<MaterialRef> materiales) {
        if (solicitante == null) {
            throw new BoletaInvalidaException("El empleado solicitante es obligatorio.");
        }
        if (entregador == null) {
            throw new BoletaInvalidaException("La persona que entrega es obligatoria.");
        }
        if (solicitante.equals(entregador)) {
            throw new BoletaInvalidaException("El solicitante y el entregador no pueden ser la misma persona.");
        }
        if (materiales == null || materiales.isEmpty()) {
            throw new BoletaInvalidaException("La boleta debe incluir al menos un material con su cantidad.");
        }
    }

    private static String generarFolioUnico() {
        return "BOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Getters de solo lectura
    public Integer getId() {
        return id;
    }

    public String getFolio() {
        return folio;
    }

    public EmpleadoRef getSolicitante() {
        return solicitante;
    }

    public EmpleadoRef getEntregador() {
        return entregador;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoBoleta getEstado() {
        return estado;
    }

    public List<MaterialRef> getMateriales() {
        return Collections.unmodifiableList(materiales);
    }
}
