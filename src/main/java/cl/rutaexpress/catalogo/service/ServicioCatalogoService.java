package cl.rutaexpress.catalogo.service;

import cl.rutaexpress.catalogo.dto.ActualizarServicioRequest;
import cl.rutaexpress.catalogo.dto.CrearServicioRequest;
import cl.rutaexpress.catalogo.dto.ServicioResponse;
import cl.rutaexpress.catalogo.entity.ServicioCatalogo;
import cl.rutaexpress.catalogo.exception.CapacidadAgotadaException;
import cl.rutaexpress.catalogo.exception.CodigoDuplicadoException;
import cl.rutaexpress.catalogo.exception.ServicioNoEncontradoException;
import cl.rutaexpress.catalogo.repository.ServicioCatalogoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicioCatalogoService {

    private final ServicioCatalogoRepository repository;

    public ServicioCatalogoService(ServicioCatalogoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ServicioResponse> listarServiciosActivos() {
        return repository.findByActivoTrue()
                .stream()
                .map(ServicioResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServicioResponse obtenerPorId(Long id) {
        return repository.findById(id)
                .map(ServicioResponse::fromEntity)
                .orElseThrow(() -> new ServicioNoEncontradoException("Servicio con ID " + id + " no encontrado"));
    }

    @Transactional
    public ServicioResponse crearServicio(CrearServicioRequest request) {
        String codigoNormalizado = request.codigo().trim().toUpperCase();
        if (repository.existsByCodigo(codigoNormalizado)) {
            throw new CodigoDuplicadoException("Ya existe un servicio registrado con el codigo: " + codigoNormalizado);
        }

        ServicioCatalogo entidad = new ServicioCatalogo(
                codigoNormalizado,
                request.nombre().trim(),
                request.descripcion() != null ? request.descripcion().trim() : null,
                request.tipoVehiculo().trim().toUpperCase(),
                request.tarifaBase(),
                request.precioPorKg(),
                request.capacidadMaximaDiaria()
        );

        ServicioCatalogo guardado = repository.save(entidad);
        return ServicioResponse.fromEntity(guardado);
    }

    @Transactional
    public ServicioResponse actualizarServicio(Long id, ActualizarServicioRequest request) {
        ServicioCatalogo entidad = repository.findById(id)
                .orElseThrow(() -> new ServicioNoEncontradoException("Servicio con ID " + id + " no encontrado"));

        entidad.setNombre(request.nombre().trim());
        entidad.setDescripcion(request.descripcion() != null ? request.descripcion().trim() : null);
        entidad.setTipoVehiculo(request.tipoVehiculo().trim().toUpperCase());
        entidad.setTarifaBase(request.tarifaBase());
        entidad.setPrecioPorKg(request.precioPorKg());

        // Si se modifica la capacidad maxima diaria
        if (!entidad.getCapacidadMaximaDiaria().equals(request.capacidadMaximaDiaria())) {
            int diferencia = request.capacidadMaximaDiaria() - entidad.getCapacidadMaximaDiaria();
            entidad.setCapacidadMaximaDiaria(request.capacidadMaximaDiaria());
            int nuevaDisponible = entidad.getCapacidadDisponible() + diferencia;
            if (nuevaDisponible < 0) {
                nuevaDisponible = 0;
            } else if (nuevaDisponible > request.capacidadMaximaDiaria()) {
                nuevaDisponible = request.capacidadMaximaDiaria();
            }
            entidad.setCapacidadDisponible(nuevaDisponible);
        }

        if (request.activo() != null) {
            entidad.setActivo(request.activo());
        }

        ServicioCatalogo actualizado = repository.save(entidad);
        return ServicioResponse.fromEntity(actualizado);
    }

    /**
     * Descuento atomico y thread-safe de capacidad vehicular.
     * Si la capacidad es 0, lanza CapacidadAgotadaException (HTTP 409 Conflict).
     */
    @Transactional
    public ServicioResponse descontarCapacidad(Long id) {
        int filasModificadas = repository.descontarCapacidadAtomica(id);

        if (filasModificadas == 0) {
            ServicioCatalogo servicio = repository.findById(id)
                    .orElseThrow(() -> new ServicioNoEncontradoException("Servicio con ID " + id + " no encontrado"));

            if (!servicio.getActivo()) {
                throw new CapacidadAgotadaException("El servicio '" + servicio.getNombre() + "' se encuentra inactivo");
            }

            if (servicio.getCapacidadDisponible() <= 0) {
                throw new CapacidadAgotadaException("Capacidad de flota agotada para el servicio: " + servicio.getNombre());
            }
        }

        return repository.findById(id)
                .map(ServicioResponse::fromEntity)
                .orElseThrow(() -> new ServicioNoEncontradoException("Servicio con ID " + id + " no encontrado"));
    }

    @Transactional
    public ServicioResponse restablecerCapacidad(Long id) {
        ServicioCatalogo entidad = repository.findById(id)
                .orElseThrow(() -> new ServicioNoEncontradoException("Servicio con ID " + id + " no encontrado"));

        entidad.setCapacidadDisponible(entidad.getCapacidadMaximaDiaria());
        ServicioCatalogo guardado = repository.save(entidad);
        return ServicioResponse.fromEntity(guardado);
    }
}
