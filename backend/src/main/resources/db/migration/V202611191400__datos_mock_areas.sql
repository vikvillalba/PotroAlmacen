-- Datos mock de áreas (módulo Áreas): cada área con sus espacios.
-- No incluye materiales permitidos (are_area_material): los materiales son del módulo Materiales y se asignan a cada
-- área desde la aplicación (POST /api/areas/{id}/materiales) una vez que existen en el inventario.

INSERT INTO are_area (nombre_area, tipo_area, metros_cuadrados) VALUES
    ('Edificio Académico A', 'ACADEMICA', 850.00),
    ('Laboratorio de Cómputo', 'LABORATORIO', 120.50),
    ('Oficinas Administrativas', 'ADMINISTRATIVA', 300.00),
    ('Almacén General', 'ALMACEN', 200.00),
    ('Gimnasio Universitario', 'INSTALACIONES_DEPORTIVAS', 1200.00);

-- Espacios
INSERT INTO are_espacio (area_id, nombre_espacio, descripcion, frecuencia_limpieza)
SELECT a.id, e.nombre_espacio, e.descripcion, e.frecuencia_limpieza
FROM are_area a
JOIN (
    SELECT 'Edificio Académico A' AS nombre_area, 'Aula 101' AS nombre_espacio, 'Aula de clases, planta baja' AS descripcion, 'DIARIA' AS frecuencia_limpieza
    UNION ALL SELECT 'Edificio Académico A', 'Aula 102', 'Aula de clases, planta baja', 'DIARIA'
    UNION ALL SELECT 'Edificio Académico A', 'Baños planta baja', 'Sanitarios de hombres y mujeres', 'TRES_VECES_AL_DIA'
    UNION ALL SELECT 'Edificio Académico A', 'Oficina de coordinación', 'Coordinación de carrera', 'DIARIA'
    UNION ALL SELECT 'Laboratorio de Cómputo', 'Laboratorio 1', 'Equipos de cómputo para prácticas', 'DIARIA'
    UNION ALL SELECT 'Laboratorio de Cómputo', 'Cubículo de soporte', 'Resguardo de equipo y soporte técnico', 'SEMANAL'
    UNION ALL SELECT 'Oficinas Administrativas', 'Oficina de dirección', 'Dirección general', 'DIARIA'
    UNION ALL SELECT 'Oficinas Administrativas', 'Sala de juntas', 'Reuniones del personal', 'CADA_TRES_DIAS'
    UNION ALL SELECT 'Oficinas Administrativas', 'Baños administrativos', 'Sanitarios del área administrativa', 'DOS_VECES_AL_DIA'
    UNION ALL SELECT 'Almacén General', 'Bodega principal', 'Resguardo de materiales de limpieza', 'QUINCENAL'
    UNION ALL SELECT 'Gimnasio Universitario', 'Cancha techada', 'Cancha de usos múltiples', 'DIARIA'
    UNION ALL SELECT 'Gimnasio Universitario', 'Vestidores', 'Vestidores y regaderas', 'DOS_VECES_AL_DIA'
) e ON e.nombre_area = a.nombre_area;