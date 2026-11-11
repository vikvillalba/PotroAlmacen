CREATE TABLE mat_ficha_tecnica (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    categoria VARCHAR(120) NOT NULL,
    unidad_medida VARCHAR(60) NOT NULL,
    presentacion VARCHAR(80) NOT NULL,
    departamento VARCHAR(120) NOT NULL,
    precio_unitario DECIMAL(10, 2),
    caracteristicas_tecnicas TEXT,
    marca_requerida VARCHAR(100),
    normas VARCHAR(255),
    requiere_hoja_seguridad BOOLEAN NOT NULL DEFAULT FALSE,
    uso_y_precauciones TEXT,
    version BIGINT NOT NULL DEFAULT 0
);