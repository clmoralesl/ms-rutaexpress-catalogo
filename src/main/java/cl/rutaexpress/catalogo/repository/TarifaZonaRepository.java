package cl.rutaexpress.catalogo.repository;

import cl.rutaexpress.catalogo.entity.TarifaZona;
import cl.rutaexpress.catalogo.entity.ZonaTarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifaZonaRepository extends JpaRepository<TarifaZona, Long> {
    Optional<TarifaZona> findByZona(ZonaTarifa zona);
}
