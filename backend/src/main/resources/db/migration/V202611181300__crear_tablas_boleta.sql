-- Boleta de entrega (módulo Salidas). Sin FK hacia Personal ni Materiales: son otros módulos.
CREATE TABLE sal_boleta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    folio VARCHAR(20) NOT NULL UNIQUE,
    solicitante_id INT NOT NULL,
    solicitante_nombre VARCHAR(200) NOT NULL,
    solicitante_puesto VARCHAR(120) NULL,
    entregador_id INT NOT NULL,
    entregador_nombre VARCHAR(200) NOT NULL,
    entregador_puesto VARCHAR(120) NULL,
    fecha_hora DATETIME NOT NULL,
    estado VARCHAR(20) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    INDEX idx_sal_boleta_solicitante (solicitante_id),
    INDEX idx_sal_boleta_fecha (fecha_hora)
);

CREATE TABLE sal_boleta_detalle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    boleta_id BIGINT NOT NULL,
    codigo_material VARCHAR(50) NOT NULL,
    nombre_material VARCHAR(500) NULL,
    cantidad INT NOT NULL,
    CONSTRAINT fk_sal_boleta_detalle_boleta FOREIGN KEY (boleta_id) REFERENCES sal_boleta (id) ON DELETE CASCADE,
    INDEX idx_sal_boleta_detalle_material (codigo_material)
);
