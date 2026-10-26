package cl.rutaexpress.catalogo.dto;

import cl.rutaexpress.catalogo.entity.TarifaZona;
import cl.rutaexpress.catalogo.entity.ZonaTarifa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TarifaZonaResponse(
        Long id,
        ZonaTarifa zona,
        BigDecimal factor,
        String descripcion,
        LocalDateTime fechaActualizacion
) {
    public static TarifaZonaResponse fromEntity(TarifaZona t) {
        return new TarifaZonaResponse(
                t.getId(),
                t.getZona(),
                t.getFactor(),
                t.getDescripcion(),
                t.getFechaActualizacion()
        );
    }
}
