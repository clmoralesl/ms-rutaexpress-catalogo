package cl.rutaexpress.catalogo.repository;

import cl.rutaexpress.catalogo.entity.ServicioComunaExcluida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioComunaExcluidaRepository extends JpaRepository<ServicioComunaExcluida, Long> {
    boolean existsByServicioIdAndIdComuna(Long servicioId, Integer idComuna);
    List<ServicioComunaExcluida> findByServicioId(Long servicioId);
}
