package cl.rutaexpress.catalogo.dto;

import cl.rutaexpress.catalogo.entity.ServicioCatalogo;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServicioResponse(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        String tipoVehiculo,
        BigDecimal tarifaBase,
        BigDecimal precioPorKg,
        Integer capacidadMaximaDiaria,
        Integer capacidadDisponible,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {
    public static ServicioResponse fromEntity(ServicioCatalogo entidad) {
        return new ServicioResponse(
                entidad.getId(),
                entidad.getCodigo(),
                entidad.getNombre(),
                entidad.getDescripcion(),
                entidad.getTipoVehiculo(),
                entidad.getTarifaBase(),
                entidad.getPrecioPorKg(),
                entidad.getCapacidadMaximaDiaria(),
                entidad.getCapacidadDisponible(),
                entidad.getActivo(),
                entidad.getFechaCreacion(),
                entidad.getFechaActualizacion()
        );
    }
}
