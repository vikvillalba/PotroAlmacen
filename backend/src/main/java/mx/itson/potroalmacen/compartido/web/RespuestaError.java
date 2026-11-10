package mx.itson.potroalmacen.compartido.web;

import java.util.List;

/**
 * Formato estándar de error de la API.
 *
 * <pre>{@code
 * {
 *   "codigo": "VALIDACION",
 *   "mensaje": "La solicitud contiene campos inválidos.",
 *   "campos": [ { "campo": "nombre", "mensaje": "no debe estar vacío" } ]
 * }
 * }</pre>
 *
 * @param codigo código del error, estable para que el frontend pueda reaccionar a él
 * @param mensaje mensaje legible para el usuario
 * @param campos campos inválidos; lista vacía si el error no es de validación
 */
public record RespuestaError(String codigo, String mensaje, List<CampoInvalido> campos) {

    public RespuestaError {
        campos = campos == null ? List.of() : List.copyOf(campos);
    }

    public static RespuestaError de(String codigo, String mensaje) {
        return new RespuestaError(codigo, mensaje, List.of());
    }
}
