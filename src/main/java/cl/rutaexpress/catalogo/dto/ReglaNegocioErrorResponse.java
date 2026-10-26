package cl.rutaexpress.catalogo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReglaNegocioErrorResponse(
        String codigo,
        String mensaje,
        Object limite
) {}
