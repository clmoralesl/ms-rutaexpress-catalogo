package cl.rutaexpress.catalogo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "SUCURSALES")
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo de la sucursal es obligatorio")
    @Size(max = 20, message = "El codigo no puede exceder 20 caracteres")
    @Column(name = "CODIGO", nullable = false, unique = true, length = 20)
    private String codigo;

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 120, message = "La calle no puede exceder 120 caracteres")
    @Column(name = "CALLE", nullable = false, length = 120)
    private String calle;

    @NotBlank(message = "El numero es obligatorio")
    @Size(max = 20, message = "El numero no puede exceder 20 caracteres")
    @Column(name = "NUMERO", nullable = false, length = 20)
    private String numero;

    @Size(max = 80, message = "El complemento no puede exceder 80 caracteres")
    @Column(name = "COMPLEMENTO", length = 80)
    private String complemento;

    @NotNull(message = "El idComuna (CUT) es obligatorio")
    @Column(name = "ID_COMUNA", nullable = false)
    private Integer idComuna;

    @Size(max = 200, message = "El horario no puede exceder 200 caracteres")
    @Column(name = "HORARIO", length = 200)
    private String horario;

    @NotNull
    @Column(name = "ACTIVA", nullable = false)
    private Boolean activa = true;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    public Sucursal() {
    }

    public Sucursal(String codigo, String nombre, String calle, String numero, String complemento,
                    Integer idComuna, String horario, Boolean activa) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.calle = calle;
        this.numero = numero;
        this.complemento = complemento;
        this.idComuna = idComuna;
        this.horario = horario;
        this.activa = (activa != null) ? activa : true;
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.activa == null) {
            this.activa = true;
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
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public Integer getIdComuna() {
        return idComuna;
    }

    public void setIdComuna(Integer idComuna) {
        this.idComuna = idComuna;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
