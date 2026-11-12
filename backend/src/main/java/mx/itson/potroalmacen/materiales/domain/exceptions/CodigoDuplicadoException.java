package mx.itson.potroalmacen.materiales.domain.exceptions;

import mx.itson.potroalmacen.compartido.excepcion.DuplicadoException;

/** Ya existe un material con el mismo código. Se responde con 409. */
public class CodigoDuplicadoException extends DuplicadoException {

    public static final String CODIGO = "MATERIAL_DUPLICADO";

    private final String codigoMaterial;

    public CodigoDuplicadoException(String codigoMaterial) {
        super(CODIGO, "Ya existe un material con el código " + codigoMaterial);
        this.codigoMaterial = codigoMaterial;
    }

    /** Código del material repetido (getCodigo() es el código de error, por herencia). */
    public String getCodigoMaterial() {
        return codigoMaterial;
    }
}
