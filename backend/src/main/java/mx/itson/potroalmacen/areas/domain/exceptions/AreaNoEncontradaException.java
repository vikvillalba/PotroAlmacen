package mx.itson.potroalmacen.areas.domain.exceptions;

import mx.itson.potroalmacen.compartido.excepcion.NoEncontradoException;

public class AreaNoEncontradaException extends NoEncontradoException {

    public static final String CODIGO = "AREA_NO_ENCONTRADA";

    public AreaNoEncontradaException(Integer idArea) {
        super(CODIGO, "No existe un área con el id " + idArea);
    }
}
