package cl.rutaexpress.catalogo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SERVICIOS_CATALOGO")
public class ServicioCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo del servicio es obligatorio")
    @Size(max = 50, message = "El codigo no puede exceder 50 caracteres")
    @Column(name = "CODIGO", nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Size(max = 500, message = "La descripcion no puede exceder 500 caracteres")
    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @NotBlank(message = "El tipo de vehiculo es obligatorio")
    @Size(max = 50, message = "El tipo de vehiculo no puede exceder 50 caracteres")
    @Column(name = "TIPO_VEHICULO", nullable = false, length = 50)
    private String tipoVehiculo;

    @NotNull(message = "La tarifa base es obligatoria")
    @DecimalMin(value = "0.0", inclusive = true, message = "La tarifa base debe ser positiva o cero")
    @Column(name = "TARIFA_BASE", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaBase;

    @NotNull(message = "El precio por kilogramo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio por kilo debe ser positivo o cero")
    @Column(name = "PRECIO_POR_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPorKg;

    @NotNull(message = "La capacidad maxima diaria es obligatoria")
    @Min(value = 1, message = "La capacidad maxima debe ser al menos 1")
    @Column(name = "CAPACIDAD_MAXIMA_DIARIA", nullable = false)
    private Integer capacidadMaximaDiaria;

    @NotNull(message = "La capacidad disponible es obligatoria")
    @Min(value = 0, message = "La capacidad disponible no puede ser negativa")
    @Column(name = "CAPACIDAD_DISPONIBLE", nullable = false)
    private Integer capacidadDisponible;

    @NotNull
    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo = true;

    @Column(name = "PESO_MAXIMO_KG", precision = 6, scale = 2)
    private BigDecimal pesoMaximoKg;

    @Column(name = "LADO_MAXIMO_CM")
    private Integer ladoMaximoCm;

    @Column(name = "VOLUMEN_MAXIMO_CM3")
    private Long volumenMaximoCm3;

    @Column(name = "DIVISOR_VOLUMETRICO", nullable = false)
    private Integer divisorVolumetrico = 4000;

    @Column(name = "PLAZO_MIN_DIAS")
    private Integer plazoMinDias;

    @Column(name = "PLAZO_MAX_DIAS")
    private Integer plazoMaxDias;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    public ServicioCatalogo() {
    }

    public ServicioCatalogo(String codigo, String nombre, String descripcion, String tipoVehiculo,
                            BigDecimal tarifaBase, BigDecimal precioPorKg, Integer capacidadMaximaDiaria) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoVehiculo = tipoVehiculo;
        this.tarifaBase = tarifaBase;
        this.precioPorKg = precioPorKg;
        this.capacidadMaximaDiaria = capacidadMaximaDiaria;
        this.capacidadDisponible = capacidadMaximaDiaria;
        this.activo = true;
        this.divisorVolumetrico = 4000;
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.capacidadDisponible == null) {
            this.capacidadDisponible = this.capacidadMaximaDiaria;
        }
        if (this.divisorVolumetrico == null) {
            this.divisorVolumetrico = 4000;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo != null ? codigo.trim().toUpperCase() : null;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo != null ? tipoVehiculo.trim().toUpperCase() : null;
    }

    public BigDecimal getTarifaBase() {
        return tarifaBase;
    }

    public void setTarifaBase(BigDecimal tarifaBase) {
        this.tarifaBase = tarifaBase;
    }

    public BigDecimal getPrecioPorKg() {
        return precioPorKg;
    }

    public void setPrecioPorKg(BigDecimal precioPorKg) {
        this.precioPorKg = precioPorKg;
    }

    public Integer getCapacidadMaximaDiaria() {
        return capacidadMaximaDiaria;
    }

    public void setCapacidadMaximaDiaria(Integer capacidadMaximaDiaria) {
        this.capacidadMaximaDiaria = capacidadMaximaDiaria;
    }

    public Integer getCapacidadDisponible() {
        return capacidadDisponible;
    }

    public void setCapacidadDisponible(Integer capacidadDisponible) {
        this.capacidadDisponible = capacidadDisponible;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public BigDecimal getPesoMaximoKg() {
        return pesoMaximoKg;
    }

    public void setPesoMaximoKg(BigDecimal pesoMaximoKg) {
        this.pesoMaximoKg = pesoMaximoKg;
    }

    public Integer getLadoMaximoCm() {
        return ladoMaximoCm;
    }

    public void setLadoMaximoCm(Integer ladoMaximoCm) {
        this.ladoMaximoCm = ladoMaximoCm;
    }

    public Long getVolumenMaximoCm3() {
        return volumenMaximoCm3;
    }

    public void setVolumenMaximoCm3(Long volumenMaximoCm3) {
        this.volumenMaximoCm3 = volumenMaximoCm3;
    }

    public Integer getDivisorVolumetrico() {
        return divisorVolumetrico;
    }

    public void setDivisorVolumetrico(Integer divisorVolumetrico) {
        this.divisorVolumetrico = divisorVolumetrico;
    }

    public Integer getPlazoMinDias() {
        return plazoMinDias;
    }

    public void setPlazoMinDias(Integer plazoMinDias) {
        this.plazoMinDias = plazoMinDias;
    }

    public Integer getPlazoMaxDias() {
        return plazoMaxDias;
    }

    public void setPlazoMaxDias(Integer plazoMaxDias) {
        this.plazoMaxDias = plazoMaxDias;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
