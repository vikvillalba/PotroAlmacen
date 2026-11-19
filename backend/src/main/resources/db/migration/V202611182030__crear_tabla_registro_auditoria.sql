-- Bitácora de auditoría (módulo Auditoría). Solo se insertan registros; sin FK hacia otros módulos.
CREATE TABLE aud_registro_auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    campus VARCHAR(80) NULL,
    accion VARCHAR(20) NOT NULL,
    modulo VARCHAR(60) NULL,
    entidad VARCHAR(80) NULL,
    referencia VARCHAR(100) NULL,
    ocurrido_en DATETIME(6) NOT NULL,
    INDEX idx_aud_registro_fecha (ocurrido_en),
    INDEX idx_aud_registro_usuario (usuario_id),
    INDEX idx_aud_registro_objetivo (modulo, entidad, referencia)
);
