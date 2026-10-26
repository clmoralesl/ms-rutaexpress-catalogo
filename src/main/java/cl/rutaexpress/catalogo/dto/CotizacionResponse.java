package cl.rutaexpress.catalogo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CotizacionResponse(
        Long servicioId,
        String servicioCodigo,
        Long sucursalOrigenId,
        Integer idComunaOrigen,
        String zona,
        BigDecimal factorZona,
        BigDecimal tarifaBase,
        BigDecimal precioPorKg,
        BigDecimal pesoVolumetricoKg,
        BigDecimal pesoFacturableKg,
        Long totalClp,
        Integer plazoMinDias,
        Integer plazoMaxDias,
        LocalDateTime calculadoEn,
        DireccionSucursalResponse direccionSucursal
) {
    public record DireccionSucursalResponse(
            String nombre,
            String calle,
            String numero,
            String complemento,
            Integer idComuna
    ) {}
}
