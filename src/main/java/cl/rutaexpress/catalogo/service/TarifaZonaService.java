package cl.rutaexpress.catalogo.service;

import cl.rutaexpress.catalogo.dto.ActualizarTarifaZonaRequest;
import cl.rutaexpress.catalogo.dto.TarifaZonaResponse;
import cl.rutaexpress.catalogo.entity.TarifaZona;
import cl.rutaexpress.catalogo.entity.ZonaTarifa;
import cl.rutaexpress.catalogo.exception.ReglaNegocioException;
import cl.rutaexpress.catalogo.repository.TarifaZonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TarifaZonaService {

    private final TarifaZonaRepository tarifaZonaRepository;

    public TarifaZonaService(TarifaZonaRepository tarifaZonaRepository) {
        this.tarifaZonaRepository = tarifaZonaRepository;
    }

    public List<TarifaZonaResponse> listar() {
        return tarifaZonaRepository.findAll().stream()
                .map(TarifaZonaResponse::fromEntity)
                .toList();
    }

    public TarifaZonaResponse obtenerPorZona(ZonaTarifa zona) {
        TarifaZona tarifa = buscarEntidadPorZona(zona);
        return TarifaZonaResponse.fromEntity(tarifa);
    }

    @Transactional
    public TarifaZonaResponse actualizar(ZonaTarifa zona, ActualizarTarifaZonaRequest request) {
        TarifaZona tarifa = buscarEntidadPorZona(zona);
        tarifa.setFactor(request.factor());
        if (request.descripcion() != null) {
            tarifa.setDescripcion(request.descripcion().trim());
        }

        TarifaZona guardada = tarifaZonaRepository.save(tarifa);
        return TarifaZonaResponse.fromEntity(guardada);
    }

    public TarifaZona buscarEntidadPorZona(ZonaTarifa zona) {
        return tarifaZonaRepository.findByZona(zona)
                .orElseThrow(() -> new ReglaNegocioException(
                        "ZONA_NO_CONFIGURADA",
                        "No existe configuracion tarifaria para la zona: " + zona
                ));
    }
}
