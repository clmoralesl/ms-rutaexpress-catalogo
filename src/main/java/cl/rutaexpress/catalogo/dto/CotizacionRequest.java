package cl.rutaexpress.catalogo.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CotizacionRequest(
        @NotNull(message = "El servicioId es obligatorio")
        Long servicioId,

        @NotNull(message = "La sucursalOrigenId es obligatoria")
        Long sucursalOrigenId,

        @NotNull(message = "El idComunaDestino es obligatorio")
        Integer idComunaDestino,

        @NotNull(message = "El pesoKg es obligatorio")
        BigDecimal pesoKg,

        Integer largoCm,
        Integer anchoCm,
        Integer altoCm,

        Boolean validarSucursalActiva
) {
    public CotizacionRequest(Long servicioId, Long sucursalOrigenId, Integer idComunaDestino,
                             BigDecimal pesoKg, Integer largoCm, Integer anchoCm, Integer altoCm) {
        this(servicioId, sucursalOrigenId, idComunaDestino, pesoKg, largoCm, anchoCm, altoCm, true);
    }
}
