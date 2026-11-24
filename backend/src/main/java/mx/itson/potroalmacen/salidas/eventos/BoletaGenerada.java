package mx.itson.potroalmacen.salidas.eventos;

import java.time.Instant;
import java.util.Objects;
import mx.itson.potroalmacen.compartido.auditoria.AccionAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.EventoAuditable;

/**
 * Se publica cuando se genera una boleta de entrega de materiales.
 *
 * <p>Implementa {@link EventoAuditable}, así que Auditoría la registra en la bitácora como una acción
 * {@link AccionAuditoria#ENTREGAR} del entregador, con el folio como referencia.
 *
 * @param boletaId id asignado a la boleta.
 * @param folio folio de la boleta (por ejemplo, {@code BOL-1A2B3C4D}).
 * @param idSolicitante id del empleado que solicitó el material.
 * @param idEntregador id del empleado que entregó el material; es el usuario que se audita.
 * @param ocurridoEn momento en que se generó la boleta.
 */
public record BoletaGenerada(
        Integer boletaId, String folio, Integer idSolicitante, Integer idEntregador, Instant ocurridoEn)
        implements EventoAuditable {

    public BoletaGenerada {
        Objects.requireNonNull(boletaId, "boletaId es obligatorio");
        Objects.requireNonNull(folio, "folio es obligatorio");
        Objects.requireNonNull(idSolicitante, "idSolicitante es obligatorio");
        Objects.requireNonNull(idEntregador, "idEntregador es obligatorio");
        Objects.requireNonNull(ocurridoEn, "ocurridoEn es obligatorio");
    }

    @Override
    public Long usuarioId() {
        return idEntregador.longValue();
    }

    /** La boleta todavía no registra campus. */
    @Override
    public String campus() {
        return null;
    }

    @Override
    public AccionAuditoria accion() {
        return AccionAuditoria.ENTREGAR;
    }

    @Override
    public String modulo() {
        return "salidas";
    }

    @Override
    public String entidad() {
        return "Boleta";
    }

    @Override
    public String referencia() {
        return folio;
    }
}
