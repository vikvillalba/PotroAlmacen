package mx.itson.potroalmacen.compartido.web;

/**
 * Campo de la petición que no pasó la validación.
 *
 * @param campo nombre del campo
 * @param mensaje motivo del error
 */
public record CampoInvalido(String campo, String mensaje) {}
