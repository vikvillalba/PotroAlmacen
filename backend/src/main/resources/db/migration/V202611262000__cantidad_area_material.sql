-- Cantidad máxima de un material que se puede entregar al área en una solicitud.
-- El dominio de Áreas (MaterialRef.cantidad) ya la exige; sin esta columna se perdía al guardar y toda lectura
-- devolvía 1, así que cualquier solicitud de más de una unidad se rechazaba como "excede la asignación del área".
ALTER TABLE are_area_material
    ADD COLUMN cantidad INT NOT NULL DEFAULT 1 AFTER nombre_material,
    ADD CONSTRAINT chk_are_area_material_cantidad CHECK (cantidad > 0);