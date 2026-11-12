package mx.itson.potroalmacen.compartido.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL) // Oculta los campos nulos
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> detallesCampos // Útil para errores 400 de validación de formularios
        ) {}
