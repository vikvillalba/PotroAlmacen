package mx.itson.potroalmacen.compartido.web;

import java.util.List;
import java.util.Map;
import mx.itson.potroalmacen.compartido.excepcion.DuplicadoException;
import mx.itson.potroalmacen.compartido.excepcion.NegocioException;
import mx.itson.potroalmacen.compartido.excepcion.NoEncontradoException;
import mx.itson.potroalmacen.compartido.excepcion.PermisoDenegadoException;
import mx.itson.potroalmacen.materiales.domain.exceptions.MaterialInvalidoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Traduce las excepciones a {@link RespuestaError} para que todos los módulos respondan con el mismo formato.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    static final String CODIGO_VALIDACION = "VALIDACION";
    static final String CODIGO_ERROR_INTERNO = "ERROR_INTERNO";

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoEncontradoException.class)
    ResponseEntity<RespuestaError> manejarNoEncontrado(NoEncontradoException ex) {
        return respuestaDeNegocio(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(DuplicadoException.class)
    ResponseEntity<RespuestaError> manejarDuplicado(DuplicadoException ex) {
        return respuestaDeNegocio(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(PermisoDenegadoException.class)
    ResponseEntity<RespuestaError> manejarPermisoDenegado(PermisoDenegadoException ex) {
        return respuestaDeNegocio(HttpStatus.FORBIDDEN, ex);
    }

    //1.Integración de la excepción de dominio de Erika (Material inválido -> 400 Bad Request)
    @ExceptionHandler(MaterialInvalidoException.class)
    ResponseEntity<RespuestaError> manejarMaterialInvalido(MaterialInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RespuestaError.de(CODIGO_VALIDACION, ex.getMessage()));
    }

    //2.Manejo de IllegalArgumentException (Detecta si es duplicado o error de solicitud)
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<RespuestaError> manejarIllegalArgument(IllegalArgumentException ex) {
        boolean esDuplicado = ex.getMessage() != null &&
                (ex.getMessage().toLowerCase().contains("ya se encuentra registrado") ||
                        ex.getMessage().toLowerCase().contains("duplicado"));

        if (esDuplicado) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(RespuestaError.de("DUPLICADO", ex.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RespuestaError.de(CODIGO_VALIDACION, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<RespuestaError> manejarErrorNoPrevisto(Exception ex) {
        log.error("Error no previsto al procesar la petición", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespuestaError.de(CODIGO_ERROR_INTERNO, "Ocurrió un error inesperado. Intenta de nuevo."));
    }

    //3.Manejo de errores de validación de Spring
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new CampoInvalido(error.getField(), error.getDefaultMessage()))
                .toList();
        RespuestaError cuerpo =
                new RespuestaError(CODIGO_VALIDACION, "La solicitud contiene campos inválidos.", campos);
        return handleExceptionInternal(ex, cuerpo, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        Object cuerpo = body instanceof RespuestaError ? body : respuestaGenerica(statusCode);
        return super.handleExceptionInternal(ex, cuerpo, headers, statusCode, request);
    }

    private static ResponseEntity<RespuestaError> respuestaDeNegocio(HttpStatus status, NegocioException ex) {
        return ResponseEntity.status(status).body(RespuestaError.de(ex.getCodigo(), ex.getMessage()));
    }

    private static RespuestaError respuestaGenerica(HttpStatusCode status) {
        return switch (status.value()) {
            case 400 -> RespuestaError.de("SOLICITUD_INVALIDA", "La solicitud no es válida.");
            case 403 -> RespuestaError.de(PermisoDenegadoException.CODIGO, "No tienes permiso para esta operación.");
            case 404 -> RespuestaError.de(NoEncontradoException.CODIGO, "El recurso solicitado no existe.");
            case 405 -> RespuestaError.de("METODO_NO_PERMITIDO", "Método HTTP no permitido para este recurso.");
            case 415 -> RespuestaError.de("TIPO_NO_SOPORTADO", "El tipo de contenido no está soportado.");
            default ->
                    status.is5xxServerError()
                            ? RespuestaError.de(CODIGO_ERROR_INTERNO, "Ocurrió un error inesperado. Intenta de nuevo.")
                            : RespuestaError.de("SOLICITUD_RECHAZADA", "No se pudo procesar la solicitud.");
        };
    }
}