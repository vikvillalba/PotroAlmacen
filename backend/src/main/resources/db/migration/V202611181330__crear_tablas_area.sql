-- Áreas y espacios (módulo Áreas). Sin FK hacia Materiales: es otro módulo.
CREATE TABLE are_area (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_area VARCHAR(150) NOT NULL UNIQUE,
    tipo_area VARCHAR(40) NOT NULL,
    metros_cuadrados DECIMAL(10, 2) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE are_espacio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    area_id BIGINT NOT NULL,
    nombre_espacio VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500) NULL,
    frecuencia_limpieza VARCHAR(30) NOT NULL,
    CONSTRAINT fk_are_espacio_area FOREIGN KEY (area_id) REFERENCES are_area (id) ON DELETE CASCADE,
    INDEX idx_are_espacio_area (area_id)
);

CREATE TABLE are_area_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    area_id BIGINT NOT NULL,
    codigo_material VARCHAR(50) NOT NULL,
    nombre_material VARCHAR(500) NULL,
    CONSTRAINT fk_are_area_material_area FOREIGN KEY (area_id) REFERENCES are_area (id) ON DELETE CASCADE,
    CONSTRAINT uq_are_area_material UNIQUE (area_id, codigo_material)
);
