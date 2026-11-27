-- La validación del límite de entrega suma lo entregado a un empleado desde una fecha:
-- WHERE solicitante_id = ? AND fecha_hora >= ?. Un índice compuesto la resuelve sin recorrer todas las boletas
-- del empleado; además cubre las búsquedas solo por solicitante, así que el índice simple sobra.
CREATE INDEX idx_sal_boleta_solicitante_fecha ON sal_boleta (solicitante_id, fecha_hora);
DROP INDEX idx_sal_boleta_solicitante ON sal_boleta;
