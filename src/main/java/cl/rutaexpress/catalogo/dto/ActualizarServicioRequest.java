package cl.rutaexpress.catalogo.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ActualizarServicioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripcion no puede exceder 500 caracteres")
        String descripcion,

        @NotBlank(message = "El tipo de vehiculo es obligatorio")
        @Size(max = 50, message = "El tipo de vehiculo no puede exceder 50 caracteres")
        String tipoVehiculo,

        @NotNull(message = "La tarifa base es obligatoria")
        @DecimalMin(value = "0.0", inclusive = true, message = "La tarifa base debe ser positiva o cero")
        BigDecimal tarifaBase,

        @NotNull(message = "El precio por kilo es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio por kilo debe ser positivo o cero")
        BigDecimal precioPorKg,

        @NotNull(message = "La capacidad maxima diaria es obligatoria")
        @Min(value = 1, message = "La capacidad maxima debe ser al menos 1")
        Integer capacidadMaximaDiaria,

        Boolean activo
) {
}
