-- Categoría, unidad de medida, presentación y campus ahora se guardan como el nombre del enum
-- (por ejemplo PINTURA, LITRO, CUBETA, OBREGON_NAINARI). El detalle de la presentación va aparte.
ALTER TABLE mat_material
    ADD COLUMN contenido_presentacion VARCHAR(120) NULL AFTER presentacion;
