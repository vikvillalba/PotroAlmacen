package mx.itson.potroalmacen.salidas.domain.exceptions;

import mx.itson.potroalmacen.compartido.excepcion.NoEncontradoException;

public class BoletaNoEncontradaException extends NoEncontradoException {
    public BoletaNoEncontradaException(Integer id) {
        super("No existe la boleta " + id);
    }
}
