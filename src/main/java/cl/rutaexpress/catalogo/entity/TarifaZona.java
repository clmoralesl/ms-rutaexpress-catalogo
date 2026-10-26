package cl.rutaexpress.catalogo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TARIFA_ZONA")
public class TarifaZona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ZONA", nullable = false, unique = true, length = 30)
    private ZonaTarifa zona;

    @NotNull(message = "El factor es obligatorio")
    @DecimalMin(value = "1.0", message = "El factor multiplicador no puede ser menor a 1.0")
    @Column(name = "FACTOR", nullable = false, precision = 4, scale = 2)
    private BigDecimal factor;

    @Size(max = 200, message = "La descripcion no puede exceder 200 caracteres")
    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;

    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    public TarifaZona() {
    }

    public TarifaZona(ZonaTarifa zona, BigDecimal factor, String descripcion) {
        this.zona = zona;
        this.factor = factor;
        this.descripcion = descripcion;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonaTarifa getZona() {
        return zona;
    }

    public void setZona(ZonaTarifa zona) {
        this.zona = zona;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public void setFactor(BigDecimal factor) {
        this.factor = factor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
