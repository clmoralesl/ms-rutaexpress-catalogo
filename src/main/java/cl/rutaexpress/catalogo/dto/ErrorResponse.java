package cl.rutaexpress.catalogo.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> validaciones
) {
    public ErrorResponse(int status, String error, String mensaje, String ruta) {
        this(LocalDateTime.now(), status, error, mensaje, ruta, null);
    }

    public ErrorResponse(int status, String error, String mensaje, String ruta, Map<String, String> validaciones) {
        this(LocalDateTime.now(), status, error, mensaje, ruta, validaciones);
    }
}
