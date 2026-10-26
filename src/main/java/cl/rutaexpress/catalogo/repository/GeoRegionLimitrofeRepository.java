package cl.rutaexpress.catalogo.repository;

import cl.rutaexpress.catalogo.entity.GeoRegionLimitrofe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeoRegionLimitrofeRepository extends JpaRepository<GeoRegionLimitrofe, Long> {
    List<GeoRegionLimitrofe> findAll();
}
