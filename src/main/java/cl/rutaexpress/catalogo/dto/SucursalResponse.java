package cl.rutaexpress.catalogo.dto;

import cl.rutaexpress.catalogo.entity.Sucursal;

import java.time.LocalDateTime;

public record SucursalResponse(
        Long id,
        String codigo,
        String nombre,
        String calle,
        String numero,
        String complemento,
        Integer idComuna,
        String horario,
        Boolean activa,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {
    public static SucursalResponse fromEntity(Sucursal s) {
        return new SucursalResponse(
                s.getId(),
                s.getCodigo(),
                s.getNombre(),
                s.getCalle(),
                s.getNumero(),
                s.getComplemento(),
                s.getIdComuna(),
                s.getHorario(),
                s.getActiva(),
                s.getFechaCreacion(),
                s.getFechaActualizacion()
        );
    }
}
