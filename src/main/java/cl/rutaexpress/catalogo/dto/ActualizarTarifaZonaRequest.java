package cl.rutaexpress.catalogo.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ActualizarTarifaZonaRequest(
        @NotNull(message = "El factor es obligatorio")
        @DecimalMin(value = "1.00", message = "El factor debe ser mayor o igual a 1.00")
        @DecimalMax(value = "5.00", message = "El factor debe ser menor o igual a 5.00")
        @Digits(integer = 1, fraction = 2, message = "El factor admite como maximo 2 decimales")
        BigDecimal factor,

        @Size(max = 200, message = "La descripcion no puede exceder 200 caracteres")
        String descripcion
) {}
