CREATE TABLE org_almacen (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE org_departamento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE mat_categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE mat_unidad_medida (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE mat_presentacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE
);

-- Datos iniciales
INSERT INTO org_almacen (nombre) VALUES
    ('Obregón Náinari'),
    ('Obregón Centro'),
    ('Navojoa Sur'),
    ('Guaymas'),
    ('Empalme');

INSERT INTO org_departamento (nombre) VALUES
    ('Servicios Generales'),
    ('Mantenimiento');

INSERT INTO mat_categoria (nombre) VALUES
    ('Líquidos'),
    ('Papeles'),
    ('Bolsas'),
    ('Guantes'),
    ('Escobas');

INSERT INTO mat_unidad_medida (nombre) VALUES
    ('Litro (L)'),
    ('Mililitro (ml)'),
    ('Kilogramo (kg)'),
    ('Pieza (pza)'),
    ('Paquete'),
    ('Par');

INSERT INTO mat_presentacion (nombre) VALUES
    ('Botella'),
    ('Galón'),
    ('Garrafa'),
    ('Paquete'),
    ('Caja'),
    ('Pieza');