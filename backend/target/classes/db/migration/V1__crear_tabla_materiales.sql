CREATE TABLE mat_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    campus VARCHAR(80) NOT NULL,
    nombre VARCHAR(500) NOT NULL,
    categoria VARCHAR(120) NOT NULL,
    unidad_medida VARCHAR(60) NOT NULL,
    presentacion VARCHAR(80) NOT NULL,
    precio_unitario DECIMAL(10, 2),
    cantidad INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0,
    stock_maximo INT NULL,
    uso_precauciones TEXT,
    version BIGINT NOT NULL DEFAULT 0
);