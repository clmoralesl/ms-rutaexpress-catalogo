package cl.rutaexpress.catalogo.service;

import cl.rutaexpress.catalogo.domain.CalculadoraTarifa;
import cl.rutaexpress.catalogo.domain.ClasificadorZona;
import cl.rutaexpress.catalogo.domain.ParRegiones;
import cl.rutaexpress.catalogo.dto.CotizacionRequest;
import cl.rutaexpress.catalogo.dto.CotizacionResponse;
import cl.rutaexpress.catalogo.entity.GeoRegion;
import cl.rutaexpress.catalogo.entity.ServicioCatalogo;
import cl.rutaexpress.catalogo.entity.Sucursal;
import cl.rutaexpress.catalogo.entity.TarifaZona;
import cl.rutaexpress.catalogo.entity.ZonaTarifa;
import cl.rutaexpress.catalogo.exception.ParametroInvalidoException;
import cl.rutaexpress.catalogo.exception.ReglaNegocioException;
import cl.rutaexpress.catalogo.exception.ServicioNoEncontradoException;
import cl.rutaexpress.catalogo.exception.SucursalNoEncontradaException;
import cl.rutaexpress.catalogo.repository.GeoRegionLimitrofeRepository;
import cl.rutaexpress.catalogo.repository.GeoRegionRepository;
import cl.rutaexpress.catalogo.repository.ServicioCatalogoRepository;
import cl.rutaexpress.catalogo.repository.ServicioComunaExcluidaRepository;
import cl.rutaexpress.catalogo.repository.SucursalRepository;
import cl.rutaexpress.catalogo.repository.TarifaZonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CotizacionService {

    private static final BigDecimal PESO_MAXIMO_ABSOLUTO = new BigDecimal("500.0");
    private static final int MEDIDA_MINIMA = 1;
    private static final int MEDIDA_MAXIMA_ABSOLUTA = 300;

    private final ServicioCatalogoRepository servicioCatalogoRepository;
    private final SucursalRepository sucursalRepository;
    private final GeoRegionRepository geoRegionRepository;
    private final GeoRegionLimitrofeRepository geoRegionLimitrofeRepository;
    private final TarifaZonaRepository tarifaZonaRepository;
    private final ServicioComunaExcluidaRepository servicioComunaExcluidaRepository;

    public CotizacionService(
            ServicioCatalogoRepository servicioCatalogoRepository,
            SucursalRepository sucursalRepository,
            GeoRegionRepository geoRegionRepository,
            GeoRegionLimitrofeRepository geoRegionLimitrofeRepository,
            TarifaZonaRepository tarifaZonaRepository,
            ServicioComunaExcluidaRepository servicioComunaExcluidaRepository) {
        this.servicioCatalogoRepository = servicioCatalogoRepository;
        this.sucursalRepository = sucursalRepository;
        this.geoRegionRepository = geoRegionRepository;
        this.geoRegionLimitrofeRepository = geoRegionLimitrofeRepository;
        this.tarifaZonaRepository = tarifaZonaRepository;
        this.servicioComunaExcluidaRepository = servicioComunaExcluidaRepository;
    }

    public CotizacionResponse cotizar(CotizacionRequest request) {
        validarParametrosRangosAbsolutos(request);

        Sucursal sucursal = sucursalRepository.findById(request.sucursalOrigenId())
                .orElseThrow(() -> new SucursalNoEncontradaException(request.sucursalOrigenId()));

        boolean debeValidarActiva = request.validarSucursalActiva() == null || Boolean.TRUE.equals(request.validarSucursalActiva());
        if (debeValidarActiva && !Boolean.TRUE.equals(sucursal.getActiva())) {
            throw new ReglaNegocioException("SUCURSAL_INACTIVA", "La sucursal seleccionada no se encuentra activa");
        }

        ServicioCatalogo servicio = servicioCatalogoRepository.findById(request.servicioId())
                .orElseThrow(() -> new ServicioNoEncontradoException(request.servicioId()));

        if (!Boolean.TRUE.equals(servicio.getActivo())) {
            throw new ReglaNegocioException("SERVICIO_INACTIVO", "El servicio " + servicio.getCodigo() + " no se encuentra activo");
        }

        if (servicioComunaExcluidaRepository.existsByServicioIdAndIdComuna(servicio.getId(), request.idComunaDestino())) {
            throw new ReglaNegocioException(
                    "COMUNA_NO_CUBIERTA",
                    "La comuna de destino (" + request.idComunaDestino() + ") no cuenta con cobertura para el servicio " + servicio.getCodigo(),
                    request.idComunaDestino()
            );
        }

        // Clasificar Zona
        Set<Integer> regionesExtremas = geoRegionRepository.findByEsExtremaTrue().stream()
                .map(GeoRegion::getIdRegion)
                .collect(Collectors.toSet());

        Set<ParRegiones> limitrofes = geoRegionLimitrofeRepository.findAll().stream()
                .map(l -> ParRegiones.de(l.getIdRegionA(), l.getIdRegionB()))
                .collect(Collectors.toSet());

        ZonaTarifa zona = ClasificadorZona.clasificar(
                sucursal.getIdComuna(),
                request.idComunaDestino(),
                regionesExtremas,
                limitrofes
        );

        TarifaZona tarifaZona = tarifaZonaRepository.findByZona(zona)
                .orElseThrow(() -> new IllegalStateException("Configuracion interna faltante: no existe factor configurado para la zona " + zona));

        BigDecimal factorZona = tarifaZona.getFactor();

        // Calcular Tarifa
        CalculadoraTarifa.Entrada entradaCalculo = new CalculadoraTarifa.Entrada(
                servicio.getTarifaBase(),
                servicio.getPrecioPorKg(),
                factorZona,
                request.pesoKg(),
                request.largoCm(),
                request.anchoCm(),
                request.altoCm(),
                servicio.getDivisorVolumetrico() != null ? servicio.getDivisorVolumetrico() : 4000
        );

        CalculadoraTarifa.Resultado resultado = CalculadoraTarifa.calcular(entradaCalculo);

        // Validar Limites del Servicio (M1: se compara peso real contra el limite maximo del servicio)
        validarLimitesServicio(servicio, request.pesoKg(), request.largoCm(), request.anchoCm(), request.altoCm());

        return new CotizacionResponse(
                servicio.getId(),
                servicio.getCodigo(),
                sucursal.getId(),
                sucursal.getIdComuna(),
                zona.name(),
                factorZona,
                servicio.getTarifaBase(),
                servicio.getPrecioPorKg(),
                resultado.pesoVolumetricoKg(),
                resultado.pesoFacturableKg(),
                resultado.totalClp(),
                servicio.getPlazoMinDias(),
                servicio.getPlazoMaxDias(),
                LocalDateTime.now(),
                new CotizacionResponse.DireccionSucursalResponse(
                        sucursal.getNombre(),
                        sucursal.getCalle(),
                        sucursal.getNumero(),
                        sucursal.getComplemento(),
                        sucursal.getIdComuna()
                )
        );
    }

    private void validarParametrosRangosAbsolutos(CotizacionRequest req) {
        if (req.pesoKg() == null || req.pesoKg().compareTo(BigDecimal.ZERO) <= 0 || req.pesoKg().compareTo(PESO_MAXIMO_ABSOLUTO) > 0) {
            throw new ParametroInvalidoException("El peso debe ser mayor a 0 y menor o igual a " + PESO_MAXIMO_ABSOLUTO + " kg");
        }

        boolean tieneLargo = req.largoCm() != null;
        boolean tieneAncho = req.anchoCm() != null;
        boolean tieneAlto = req.altoCm() != null;

        // Caso E8: o vienen las tres dimensiones o ninguna
        if ((tieneLargo || tieneAncho || tieneAlto) && !(tieneLargo && tieneAncho && tieneAlto)) {
            throw new ParametroInvalidoException("Las dimensiones (largo, ancho y alto) deben enviarse conjuntamente o no enviarse");
        }

        if (tieneLargo) {
            if (req.largoCm() < MEDIDA_MINIMA || req.largoCm() > MEDIDA_MAXIMA_ABSOLUTA
                    || req.anchoCm() < MEDIDA_MINIMA || req.anchoCm() > MEDIDA_MAXIMA_ABSOLUTA
                    || req.altoCm() < MEDIDA_MINIMA || req.altoCm() > MEDIDA_MAXIMA_ABSOLUTA) {
                throw new ParametroInvalidoException("Las dimensiones deben estar en el rango de " + MEDIDA_MINIMA + " a " + MEDIDA_MAXIMA_ABSOLUTA + " cm");
            }
        }

        if (req.idComunaDestino() == null) {
            throw new ParametroInvalidoException("El identificador de comuna de destino es obligatorio");
        }

        int regionDestino = req.idComunaDestino() / 1000;
        if (!geoRegionRepository.existsById(regionDestino)) {
            throw new ParametroInvalidoException("Comuna de destino no valida o region inexistente: " + req.idComunaDestino());
        }
    }

    private void validarLimitesServicio(
            ServicioCatalogo servicio,
            BigDecimal pesoReal,
            Integer largoCm,
            Integer anchoCm,
            Integer altoCm) {

        if (servicio.getPesoMaximoKg() != null && pesoReal.compareTo(servicio.getPesoMaximoKg()) > 0) {
            throw new ReglaNegocioException(
                    "PESO_EXCEDE_LIMITE",
                    "El peso real (" + pesoReal + " kg) excede el limite maximo permitido de " + servicio.getPesoMaximoKg() + " kg para el servicio " + servicio.getCodigo(),
                    servicio.getPesoMaximoKg()
            );
        }

        if (largoCm != null && anchoCm != null && altoCm != null) {
            int mayorLado = Math.max(largoCm, Math.max(anchoCm, altoCm));
            if (servicio.getLadoMaximoCm() != null && mayorLado > servicio.getLadoMaximoCm()) {
                throw new ReglaNegocioException(
                    "DIMENSION_EXCEDE_LIMITE",
                    "El lado mayor (" + mayorLado + " cm) excede el limite maximo de " + servicio.getLadoMaximoCm() + " cm para el servicio " + servicio.getCodigo(),
                    servicio.getLadoMaximoCm()
                );
            }

            long volumenCm3 = (long) largoCm * anchoCm * altoCm;
            if (servicio.getVolumenMaximoCm3() != null && volumenCm3 > servicio.getVolumenMaximoCm3()) {
                throw new ReglaNegocioException(
                    "DIMENSION_EXCEDE_LIMITE",
                    "El volumen (" + volumenCm3 + " cm3) excede el limite maximo de " + servicio.getVolumenMaximoCm3() + " cm3 para el servicio " + servicio.getCodigo(),
                    servicio.getVolumenMaximoCm3()
                );
            }
        }
    }
}
