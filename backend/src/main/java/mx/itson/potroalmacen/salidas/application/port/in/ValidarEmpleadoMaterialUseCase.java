package mx.itson.potroalmacen.salidas.application.port.in;

public interface ValidarEmpleadoMaterialUseCase {
    /**
     * Se valida el límite de entrega del empleado
     * @param solicitud Solicitud de validación
     */
    void validarEmpleado(ValidarEmpleadoMaterialCommand solicitud);
}
