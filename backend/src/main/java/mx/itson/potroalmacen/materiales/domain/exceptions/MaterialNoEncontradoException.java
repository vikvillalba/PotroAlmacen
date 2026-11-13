package mx.itson.potroalmacen.materiales.domain.exceptions;

/** No existe un material con el id solicitado. */
public class MaterialNoEncontradoException extends RuntimeException {

    private final Integer id;

    public MaterialNoEncontradoException(Integer id) {
        super("No existe un material con el id " + id);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
