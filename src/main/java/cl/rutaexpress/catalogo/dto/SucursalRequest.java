package cl.rutaexpress.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SucursalRequest(
        @NotBlank(message = "El codigo es obligatorio")
        @Size(max = 20, message = "El codigo no puede exceder 20 caracteres")
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 120, message = "La calle no puede exceder 120 caracteres")
        String calle,

        @NotBlank(message = "El numero es obligatorio")
        @Size(max = 20, message = "El numero no puede exceder 20 caracteres")
        String numero,

        @Size(max = 80, message = "El complemento no puede exceder 80 caracteres")
        String complemento,

        @NotNull(message = "El idComuna es obligatorio")
        Integer idComuna,

        @Size(max = 200, message = "El horario no puede exceder 200 caracteres")
        String horario,

        Boolean activa
) {}
