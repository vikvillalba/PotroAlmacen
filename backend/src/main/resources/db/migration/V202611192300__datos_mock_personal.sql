-- Datos mock de personal (módulo Personal): empleados internos (ITSON) y externos, con sus áreas asignadas.
-- Los ids salen en el orden de este script (1 a 9) si per_empleado está vacía. Hay un empleado inactivo (7) y
-- uno en préstamo (4) para probar las validaciones. Las áreas son las de datos_mock_areas; no hay FK porque son
-- de otro módulo, solo se guarda el id y el nombre del área.

INSERT INTO per_empleado
    (nombre, apellido_paterno, apellido_materno, tipo_empleado, puesto, jefe_directo, telefono, activo, en_prestamo)
VALUES
    ('María', 'López', 'Hernández', 'ITSON', 'Intendente', 'Roberto Salas Mendoza', '6441234501', TRUE, FALSE),
    ('José', 'Ramírez', 'Torres', 'ITSON', 'Intendente', 'Roberto Salas Mendoza', '6441234502', TRUE, FALSE),
    ('Ana', 'Beltrán', NULL, 'ITSON', 'Auxiliar de mantenimiento', 'Roberto Salas Mendoza', '6441234503', TRUE, FALSE),
    ('Luis', 'Castro', 'Valenzuela', 'ITSON', 'Jardinero', 'Roberto Salas Mendoza', '6441234504', TRUE, TRUE),
    ('Carmen', 'Ortega', 'Félix', 'ITSON', 'Coordinadora de limpieza', 'Patricia Gómez Duarte', '6441234505', TRUE, FALSE),
    ('Jorge', 'Mendoza', 'Ruiz', 'ITSON', 'Auxiliar de gimnasio', 'Patricia Gómez Duarte', NULL, TRUE, FALSE),
    ('Rosa', 'Valdez', 'Soto', 'ITSON', 'Intendente', 'Roberto Salas Mendoza', '6441234507', FALSE, FALSE),
    ('Daniel', 'Acosta', 'Peña', 'EXTERNO', 'Contratista de limpieza', NULL, '6449876501', TRUE, FALSE),
    ('Sofía', 'Rendón', NULL, 'EXTERNO', 'Contratista de mantenimiento', NULL, '6449876502', TRUE, FALSE);

-- Áreas asignadas (el empleado se identifica por nombre y apellidos; el área, por su nombre)
INSERT INTO per_empleado_area (empleado_id, area_id, nombre_area)
SELECT e.id, a.id, a.nombre_area
FROM (
    SELECT 'María' AS nombre, 'López' AS apellido_paterno, 'Edificio Académico A' AS nombre_area
    UNION ALL SELECT 'María', 'López', 'Oficinas Administrativas'
    UNION ALL SELECT 'José', 'Ramírez', 'Edificio Académico A'
    UNION ALL SELECT 'Ana', 'Beltrán', 'Laboratorio de Cómputo'
    UNION ALL SELECT 'Ana', 'Beltrán', 'Almacén General'
    UNION ALL SELECT 'Luis', 'Castro', 'Gimnasio Universitario'
    UNION ALL SELECT 'Carmen', 'Ortega', 'Edificio Académico A'
    UNION ALL SELECT 'Carmen', 'Ortega', 'Laboratorio de Cómputo'
    UNION ALL SELECT 'Carmen', 'Ortega', 'Oficinas Administrativas'
    UNION ALL SELECT 'Jorge', 'Mendoza', 'Gimnasio Universitario'
    UNION ALL SELECT 'Rosa', 'Valdez', 'Oficinas Administrativas'
    UNION ALL SELECT 'Daniel', 'Acosta', 'Edificio Académico A'
    UNION ALL SELECT 'Sofía', 'Rendón', 'Almacén General'
) x
JOIN per_empleado e ON e.nombre = x.nombre AND e.apellido_paterno = x.apellido_paterno
JOIN are_area a ON a.nombre_area = x.nombre_area;
