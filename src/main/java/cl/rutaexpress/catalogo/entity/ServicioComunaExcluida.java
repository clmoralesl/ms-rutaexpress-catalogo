package cl.rutaexpress.catalogo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "SERVICIO_COMUNA_EXCLUIDA", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_SERVICIO_COMUNA_EXCLUIDA", columnNames = {"SERVICIO_ID", "ID_COMUNA"})
})
public class ServicioComunaExcluida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "SERVICIO_ID", nullable = false)
    private Long servicioId;

    @NotNull
    @Column(name = "ID_COMUNA", nullable = false)
    private Integer idComuna;

    public ServicioComunaExcluida() {
    }

    public ServicioComunaExcluida(Long servicioId, Integer idComuna) {
        this.servicioId = servicioId;
        this.idComuna = idComuna;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }

    public Integer getIdComuna() {
        return idComuna;
    }

    public void setIdComuna(Integer idComuna) {
        this.idComuna = idComuna;
    }
}
