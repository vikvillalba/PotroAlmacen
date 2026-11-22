package mx.itson.potroalmacen.auditoria.application.port.out;

import mx.itson.potroalmacen.auditoria.domain.RegistroAuditoria;

/**
 * Puerto de salida para la persistencia de la bitácora de auditoría.
 *
 * <p>La bitácora es de solo inserción: no hay operaciones para actualizar ni borrar registros.
 */
public interface RegistroAuditoriaRepositoryPort {

    /**
     * Guarda un registro nuevo en la bitácora.
     *
     * @param registro registro a guardar, sin id.
     * @return el registro guardado, con el id asignado.
     */
    RegistroAuditoria guardar(RegistroAuditoria registro);
}
