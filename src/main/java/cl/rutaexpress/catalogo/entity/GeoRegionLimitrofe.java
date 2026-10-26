package cl.rutaexpress.catalogo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "GEO_REGION_LIMITROFE", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_GEO_REGION_LIMITROFE", columnNames = {"ID_REGION_A", "ID_REGION_B"})
})
public class GeoRegionLimitrofe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "ID_REGION_A", nullable = false)
    private Integer idRegionA;

    @NotNull
    @Column(name = "ID_REGION_B", nullable = false)
    private Integer idRegionB;

    public GeoRegionLimitrofe() {
    }

    public GeoRegionLimitrofe(Integer idRegionA, Integer idRegionB) {
        if (idRegionA != null && idRegionB != null && idRegionA > idRegionB) {
            this.idRegionA = idRegionB;
            this.idRegionB = idRegionA;
        } else {
            this.idRegionA = idRegionA;
            this.idRegionB = idRegionB;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdRegionA() {
        return idRegionA;
    }

    public void setIdRegionA(Integer idRegionA) {
        this.idRegionA = idRegionA;
    }

    public Integer getIdRegionB() {
        return idRegionB;
    }

    public void setIdRegionB(Integer idRegionB) {
        this.idRegionB = idRegionB;
    }
}
