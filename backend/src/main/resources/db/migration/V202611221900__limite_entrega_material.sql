-- Límite de entrega por empleado de un material: cuánto puede recibir un empleado dentro de un periodo.
-- Los dos valores se indican juntos; NULL en ambos significa que el material no tiene límite.
ALTER TABLE mat_material
    ADD COLUMN limite_entrega_empleado INT NULL AFTER uso_precauciones,
    ADD COLUMN periodo_limite_dias INT NULL AFTER limite_entrega_empleado,
    ADD CONSTRAINT chk_mat_material_limite_entrega CHECK (
        (limite_entrega_empleado IS NULL AND periodo_limite_dias IS NULL)
        OR (limite_entrega_empleado > 0 AND periodo_limite_dias > 0)
    );
