package mx.itson.potroalmacen.auditoria.adapter.out.persistence;

import java.time.Instant;
import mx.itson.potroalmacen.auditoria.domain.RegistroAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.AccionAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.EventoAuditable;
import org.springframework.stereotype.Component;

/** Convierte entre {@link RegistroAuditoria} y {@link RegistroAuditoriaEntity}. */
@Component
public class RegistroAuditoriaMapper {

    public RegistroAuditoriaEntity toEntity(RegistroAuditoria registro) {
        if (registro == null) {
            return null;
        }
        RegistroAuditoriaEntity entity = new RegistroAuditoriaEntity();
        entity.setId(registro.getId());
        entity.setUsuarioId(registro.getUsuarioId());
        entity.setCampus(registro.getCampus());
        entity.setAccion(registro.getAccion());
        entity.setModulo(registro.getModulo());
        entity.setEntidad(registro.getEntidad());
        entity.setReferencia(registro.getReferencia());
        entity.setOcurridoEn(registro.getOcurridoEn());
        return entity;
    }

    /** El dominio solo se crea desde un evento, así que la fila se lee como uno y luego se le asigna su id. */
    public RegistroAuditoria toDomain(RegistroAuditoriaEntity entity) {
        if (entity == null) {
            return null;
        }
        EventoAuditable evento = new EventoGuardado(
                entity.getUsuarioId(),
                entity.getCampus(),
                entity.getAccion(),
                entity.getModulo(),
                entity.getEntidad(),
                entity.getReferencia(),
                entity.getOcurridoEn());
        return RegistroAuditoria.desdeEvento(evento).conId(entity.getId());
    }

    private record EventoGuardado(
            Long usuarioId,
            String campus,
            AccionAuditoria accion,
            String modulo,
            String entidad,
            String referencia,
            Instant ocurridoEn)
            implements EventoAuditable {}
}
