package cl.rutaexpress.catalogo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "GEO_REGION")
public class GeoRegion {

    @Id
    @Column(name = "ID_REGION", nullable = false)
    private Integer idRegion;

    @NotBlank(message = "El nombre de la region es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @NotNull
    @Column(name = "ES_EXTREMA", nullable = false)
    private Boolean esExtrema = false;

    public GeoRegion() {
    }

    public GeoRegion(Integer idRegion, String nombre, Boolean esExtrema) {
        this.idRegion = idRegion;
        this.nombre = nombre;
        this.esExtrema = (esExtrema != null) ? esExtrema : false;
    }

    public Integer getIdRegion() {
        return idRegion;
    }

    public void setIdRegion(Integer idRegion) {
        this.idRegion = idRegion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getEsExtrema() {
        return esExtrema;
    }

    public void setEsExtrema(Boolean esExtrema) {
        this.esExtrema = esExtrema;
    }
}
