package mx.itson.potroalmacen.auditoria.domain;

import java.time.Instant;
import mx.itson.potroalmacen.compartido.auditoria.AccionAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.EventoAuditable;
import mx.itson.potroalmacen.auditoria.domain.exceptions.AuditoriaInvalidaException;

public class RegistroAuditoria {

    private final Long id;
    private final Long usuarioId;
    private final String campus;
    private final AccionAuditoria accion;
    private final String modulo;
    private final String entidad;
    private final String referencia;
    private final Instant ocurridoEn;

    private RegistroAuditoria(
            Long id,
            Long usuarioId,
            String campus,
            AccionAuditoria accion,
            String modulo,
            String entidad,
            String referencia,
            Instant ocurridoEn) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.campus = campus;
        this.accion = accion;
        this.modulo = modulo;
        this.entrad = entidad; // o entidad
        this.referencia = referencia;
        this.ocurridoEn = ocurridoEn != null ? ocurridoEn : Instant.now();
    }

    /** Fábrica de dominio para crear un registro a partir de un evento auditable emitido por el sistema. */
    public static RegistroAuditoria desdeEvento(EventoAuditable evento) {
        if (evento == null) {
            throw new AuditoriaInvalidaException("El evento auditable no puede ser nulo.");
        }
        if (evento.usuarioId() == null) {
            throw new AuditoriaInvalidaException("El ID del usuario en la auditoría es obligatorio.");
        }
        if (evento.accion() == null) {
            throw new AuditoriaInvalidaException("La acción de auditoría es obligatoria.");
        }

        return new RegistroAuditoria(
                null,
                evento.usuarioId(),
                evento.campus(),
                evento.accion(),
                evento.modulo(),
                evento.entidad(),
                evento.referencia(),
                evento.ocurridoEn()
        );
    }

    /** Reconstituye el registro histórico con su ID desde la base de datos. */
    public RegistroAuditoria conId(Long id) {
        return new RegistroAuditoria(
                id,
                this.usuarioId,
                this.campus,
                this.accion,
                this.modulo,
                this.entidad,
                this.referencia,
                this.ocurridoEn
        );
    }

    // Getters de solo lectura
    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getCampus() { return campus; }
    public AccionAuditoria getAccion() { return accion; }
    public String getModulo() { return modulo; }
    public String getEntidad() { return entidad; }
    public String getReferencia() { return referencia; }
    public Instant getOcurridoEn() { return ocurridoEn; }
}