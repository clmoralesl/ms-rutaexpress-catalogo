package cl.rutaexpress.catalogo.repository;

import cl.rutaexpress.catalogo.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    List<Sucursal> findByActivaTrue();
    Optional<Sucursal> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
