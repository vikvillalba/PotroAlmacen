package mx.itson.potroalmacen.auditoria.application.port.in;

import mx.itson.potroalmacen.auditoria.domain.RegistroAuditoria;
import mx.itson.potroalmacen.compartido.auditoria.EventoAuditable;

/**
 * Puerto de entrada para registrar en la bitácora de auditoría una acción ocurrida en el sistema.
 */
public interface RegistrarAuditoriaUseCase {

    /**
     * Crea y guarda un registro de auditoría a partir de un evento auditable.
     *
     * @param evento evento emitido por algún módulo del sistema.
     * @return el registro guardado, con el id asignado.
     * @throws mx.itson.potroalmacen.auditoria.domain.exceptions.AuditoriaInvalidaException si el evento es nulo o le
     *     falta el usuario o la acción.
     */
    RegistroAuditoria registrar(EventoAuditable evento);
}
