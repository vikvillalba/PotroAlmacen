-- Empleados (módulo Personal). Sin FK hacia Áreas: es otro módulo.
CREATE TABLE per_empleado (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100) NULL,
    tipo_empleado VARCHAR(20) NOT NULL,
    puesto VARCHAR(120) NOT NULL,
    jefe_directo VARCHAR(200) NULL,
    telefono VARCHAR(30) NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    en_prestamo BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    INDEX idx_per_empleado_tipo (tipo_empleado),
    INDEX idx_per_empleado_activo (activo)
);

CREATE TABLE per_empleado_area (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empleado_id BIGINT NOT NULL,
    area_id INT NOT NULL,
    nombre_area VARCHAR(150) NOT NULL,
    CONSTRAINT fk_per_empleado_area_empleado FOREIGN KEY (empleado_id) REFERENCES per_empleado (id) ON DELETE CASCADE,
    CONSTRAINT uq_per_empleado_area UNIQUE (empleado_id, area_id)
);
