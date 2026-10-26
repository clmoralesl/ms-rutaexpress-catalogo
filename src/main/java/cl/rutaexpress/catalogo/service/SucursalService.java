package cl.rutaexpress.catalogo.service;

import cl.rutaexpress.catalogo.dto.SucursalRequest;
import cl.rutaexpress.catalogo.dto.SucursalResponse;
import cl.rutaexpress.catalogo.entity.Sucursal;
import cl.rutaexpress.catalogo.exception.CodigoDuplicadoException;
import cl.rutaexpress.catalogo.exception.ParametroInvalidoException;
import cl.rutaexpress.catalogo.exception.SucursalNoEncontradaException;
import cl.rutaexpress.catalogo.repository.GeoRegionRepository;
import cl.rutaexpress.catalogo.repository.SucursalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SucursalService {

    private final SucursalRepository sucursalRepository;
    private final GeoRegionRepository geoRegionRepository;

    public SucursalService(SucursalRepository sucursalRepository, GeoRegionRepository geoRegionRepository) {
        this.sucursalRepository = sucursalRepository;
        this.geoRegionRepository = geoRegionRepository;
    }

    public List<SucursalResponse> listar(Boolean soloActivas) {
        List<Sucursal> sucursales = (soloActivas != null && soloActivas)
                ? sucursalRepository.findByActivaTrue()
                : sucursalRepository.findAll();

        return sucursales.stream()
                .map(SucursalResponse::fromEntity)
                .toList();
    }

    public SucursalResponse obtenerPorId(Long id) {
        Sucursal sucursal = buscarEntidadPorId(id);
        return SucursalResponse.fromEntity(sucursal);
    }

    @Transactional
    public SucursalResponse crear(SucursalRequest request) {
        validarComuna(request.idComuna());

        String codigoNormalizado = request.codigo().trim().toUpperCase();
        if (sucursalRepository.existsByCodigo(codigoNormalizado)) {
            throw new CodigoDuplicadoException("Ya existe una sucursal con el codigo: " + codigoNormalizado);
        }

        Sucursal sucursal = new Sucursal(
                codigoNormalizado,
                request.nombre().trim(),
                request.calle().trim(),
                request.numero().trim(),
                request.complemento() != null ? request.complemento().trim() : null,
                request.idComuna(),
                request.horario() != null ? request.horario().trim() : null,
                request.activa() != null ? request.activa() : true
        );

        Sucursal guardada = sucursalRepository.save(sucursal);
        return SucursalResponse.fromEntity(guardada);
    }

    @Transactional
    public SucursalResponse actualizar(Long id, SucursalRequest request) {
        validarComuna(request.idComuna());

        Sucursal sucursal = buscarEntidadPorId(id);
        String nuevoCodigo = request.codigo().trim().toUpperCase();

        if (!sucursal.getCodigo().equalsIgnoreCase(nuevoCodigo) && sucursalRepository.existsByCodigo(nuevoCodigo)) {
            throw new CodigoDuplicadoException("Ya existe una sucursal con el codigo: " + nuevoCodigo);
        }

        sucursal.setCodigo(nuevoCodigo);
        sucursal.setNombre(request.nombre().trim());
        sucursal.setCalle(request.calle().trim());
        sucursal.setNumero(request.numero().trim());
        sucursal.setComplemento(request.complemento() != null ? request.complemento().trim() : null);
        sucursal.setIdComuna(request.idComuna());
        sucursal.setHorario(request.horario() != null ? request.horario().trim() : null);
        if (request.activa() != null) {
            sucursal.setActiva(request.activa());
        }

        Sucursal guardada = sucursalRepository.save(sucursal);
        return SucursalResponse.fromEntity(guardada);
    }

    public Sucursal buscarEntidadPorId(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new SucursalNoEncontradaException(id));
    }

    private void validarComuna(Integer idComuna) {
        if (idComuna == null || idComuna < 1001 || idComuna > 16999 || !geoRegionRepository.existsById(idComuna / 1000)) {
            throw new ParametroInvalidoException("Comuna de sucursal no valida o region inexistente: " + idComuna);
        }
    }
}
