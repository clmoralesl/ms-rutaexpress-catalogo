package cl.rutaexpress.catalogo.repository;

import cl.rutaexpress.catalogo.entity.ServicioCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioCatalogoRepository extends JpaRepository<ServicioCatalogo, Long> {

    List<ServicioCatalogo> findByActivoTrue();

    Optional<ServicioCatalogo> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    /**
     * Descuento atomico de capacidad a nivel de motor de base de datos.
     * Retorna 1 si se logro descontar (capacidadDisponible > 0 y activo = true).
     * Retorna 0 si la capacidad ya estaba agotada o el servicio esta inactivo.
     */
    @Modifying
    @Query("UPDATE ServicioCatalogo s SET s.capacidadDisponible = s.capacidadDisponible - 1, s.fechaActualizacion = CURRENT_TIMESTAMP WHERE s.id = :id AND s.capacidadDisponible > 0 AND s.activo = true")
    int descontarCapacidadAtomica(@Param("id") Long id);

    /**
     * Incremento atomico de capacidad para cancelaciones o devoluciones.
     */
    @Modifying
    @Query("UPDATE ServicioCatalogo s SET s.capacidadDisponible = CASE WHEN s.capacidadDisponible + 1 > s.capacidadMaximaDiaria THEN s.capacidadMaximaDiaria ELSE s.capacidadDisponible + 1 END, s.fechaActualizacion = CURRENT_TIMESTAMP WHERE s.id = :id AND s.activo = true")
    int incrementarCapacidadAtomica(@Param("id") Long id);
}
