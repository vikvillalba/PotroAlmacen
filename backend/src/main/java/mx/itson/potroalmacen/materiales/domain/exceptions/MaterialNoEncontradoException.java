package mx.itson.potroalmacen.materiales.domain.exceptions;

import mx.itson.potroalmacen.compartido.excepcion.NoEncontradoException;

/** No existe un material con el id o el código solicitado. Se responde con 404. */
public class MaterialNoEncontradoException extends NoEncontradoException {

    public static final String CODIGO = "MATERIAL_NO_ENCONTRADO";

    private final Integer id;

    public MaterialNoEncontradoException(Integer id) {
        super(CODIGO, "No existe un material con el id " + id);
        this.id = id;
    }

    public MaterialNoEncontradoException(String codigoMaterial) {
        super(CODIGO, "No existe un material con el código " + codigoMaterial);
        this.id = null;
    }

    /** Id buscado; {@code null} si la búsqueda fue por código. */
    public Integer getId() {
        return id;
    }
}
