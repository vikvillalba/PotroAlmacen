package mx.itson.potroalmacen.salidas.domain.exceptions;

import mx.itson.potroalmacen.compartido.excepcion.NoEncontradoException;

/** No existe un empleado con el id indicado en la solicitud. Se responde con 404. */
public class EmpleadoNoEncontradoException extends NoEncontradoException {

    public static final String CODIGO = "EMPLEADO_NO_ENCONTRADO";

    public EmpleadoNoEncontradoException(Integer idEmpleado) {
        super(CODIGO, "No existe un empleado con el id " + idEmpleado);
    }
}
