package cl.rutaexpress.catalogo.repository;

import cl.rutaexpress.catalogo.entity.GeoRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeoRegionRepository extends JpaRepository<GeoRegion, Integer> {
    List<GeoRegion> findByEsExtremaTrue();
}
