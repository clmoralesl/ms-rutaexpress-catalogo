package cl.rutaexpress.catalogo.controller;

import cl.rutaexpress.catalogo.dto.CotizacionRequest;
import cl.rutaexpress.catalogo.entity.*;
import cl.rutaexpress.catalogo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CotizacionControllerTest {

    private static final String SECRETO = "test-secreto-interno-catalogo-seguro-32caracteres";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServicioCatalogoRepository servicioRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private GeoRegionRepository regionRepository;

    @Autowired
    private GeoRegionLimitrofeRepository limitrofeRepository;

    @Autowired
    private TarifaZonaRepository tarifaZonaRepository;

    @Autowired
    private ServicioComunaExcluidaRepository excluidaRepository;

    private ServicioCatalogo servicioExpress;
    private ServicioCatalogo servicioInactivo;
    private Sucursal sucursalStgo;
    private Sucursal sucursalInactiva;

    @BeforeEach
    void setUp() {
        excluidaRepository.deleteAll();
        servicioRepository.deleteAll();
        sucursalRepository.deleteAll();
        limitrofeRepository.deleteAll();
        tarifaZonaRepository.deleteAll();
        regionRepository.deleteAll();

        // Regiones
        regionRepository.save(new GeoRegion(5, "Valparaíso", false));
        regionRepository.save(new GeoRegion(8, "Biobío", false));
        regionRepository.save(new GeoRegion(13, "Metropolitana", false));
        regionRepository.save(new GeoRegion(15, "Arica y Parinacota", true));

        // Limitrofes
        limitrofeRepository.save(new GeoRegionLimitrofe(5, 13));

        // Tarifas por Zona
        tarifaZonaRepository.save(new TarifaZona(ZonaTarifa.MISMA_COMUNA, new BigDecimal("1.00"), "Misma comuna"));
        tarifaZonaRepository.save(new TarifaZona(ZonaTarifa.MISMA_REGION, new BigDecimal("1.15"), "Misma region"));
        tarifaZonaRepository.save(new TarifaZona(ZonaTarifa.REGION_LIMITROFE, new BigDecimal("1.35"), "Region limitrofe"));
        tarifaZonaRepository.save(new TarifaZona(ZonaTarifa.INTERREGIONAL, new BigDecimal("1.70"), "Interregional"));
        tarifaZonaRepository.save(new TarifaZona(ZonaTarifa.EXTREMA, new BigDecimal("2.20"), "Extrema"));

        // Sucursal Santiago (Activa)
        sucursalStgo = sucursalRepository.save(new Sucursal(
                "SUC-STGO", "Santiago Centro", "Moneda", "1122", "Of. 101", 13101, "09:00-19:00", true
        ));

        // Sucursal Inactiva
        sucursalInactiva = sucursalRepository.save(new Sucursal(
                "SUC-INACT", "Santiago Antigua", "Ahumada", "55", null, 13101, "Cerrada", false
        ));

        // Servicio Express
        servicioExpress = new ServicioCatalogo(
                "EXPRESS", "Express 24h", "Rapido", "MOTO",
                new BigDecimal("3500.00"), new BigDecimal("900.00"), 50
        );
        servicioExpress.setPesoMaximoKg(new BigDecimal("15.00"));
        servicioExpress.setLadoMaximoCm(60);
        servicioExpress.setVolumenMaximoCm3(80000L);
        servicioExpress.setDivisorVolumetrico(4000);
        servicioExpress.setPlazoMinDias(1);
        servicioExpress.setPlazoMaxDias(1);
        servicioExpress = servicioRepository.save(servicioExpress);

        // Servicio Inactivo
        servicioInactivo = new ServicioCatalogo(
                "INACTIVO", "Servicio Inactivo", "Desc", "MOTO",
                new BigDecimal("1000.00"), new BigDecimal("500.00"), 10
        );
        servicioInactivo.setActivo(false);
        servicioInactivo = servicioRepository.save(servicioInactivo);

        // Comuna excluida (5201 en region 5)
        excluidaRepository.save(new ServicioComunaExcluida(servicioExpress.getId(), 5201));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - 200 OK Caso de Referencia Interregional: 15130 CLP")
    void testCotizacionExitosaCasoReferencia() throws Exception {
        CotizacionRequest request = new CotizacionRequest(
                servicioExpress.getId(),
                sucursalStgo.getId(),
                8101, // Concepcion (Region 8) -> Interregional (1.70)
                new BigDecimal("3.20"),
                40,
                30,
                20
        );

        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicioCodigo").value("EXPRESS"))
                .andExpect(jsonPath("$.zona").value("INTERREGIONAL"))
                .andExpect(jsonPath("$.factorZona").value(1.70))
                .andExpect(jsonPath("$.tarifaBase").value(3500.00))
                .andExpect(jsonPath("$.precioPorKg").value(900.00))
                .andExpect(jsonPath("$.pesoVolumetricoKg").value(6.0000))
                .andExpect(jsonPath("$.pesoFacturableKg").value(6.0))
                .andExpect(jsonPath("$.totalClp").value(15130))
                .andExpect(jsonPath("$.plazoMinDias").value(1))
                .andExpect(jsonPath("$.direccionSucursal.nombre").value("Santiago Centro"))
                .andExpect(jsonPath("$.direccionSucursal.idComuna").value(13101));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - 401 Unauthorized sin cabecera X-Servicio-Interno")
    void testCotizacionSinCabeceraRetorna401() throws Exception {
        CotizacionRequest request = new CotizacionRequest(
                servicioExpress.getId(),
                sucursalStgo.getId(),
                8101,
                new BigDecimal("2.0"),
                null, null, null
        );

        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - 400 Bad Request por JSON mal formado (E-A6)")
    void testCotizacionJsonMalFormadoRetorna400() throws Exception {
        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"servicioId\": \"no-es-un-numero\", "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("SOLICITUD_INVALIDA"));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - 400 Bad Request por peso <= 0 o > 500 kg")
    void testCotizacionPesoInvalidoRetorna400() throws Exception {
        CotizacionRequest reqCero = new CotizacionRequest(
                servicioExpress.getId(), sucursalStgo.getId(), 8101, BigDecimal.ZERO, null, null, null
        );
        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqCero)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PARAMETROS_INVALIDOS"));

        CotizacionRequest reqExcesivo = new CotizacionRequest(
                servicioExpress.getId(), sucursalStgo.getId(), 8101, new BigDecimal("501.0"), null, null, null
        );
        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqExcesivo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PARAMETROS_INVALIDOS"));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - 400 Bad Request por medidas parciales (Caso E8)")
    void testCotizacionMedidasParcialesRetorna400() throws Exception {
        CotizacionRequest reqParcial = new CotizacionRequest(
                servicioExpress.getId(), sucursalStgo.getId(), 8101, new BigDecimal("2.0"), 30, null, null
        );

        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqParcial)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PARAMETROS_INVALIDOS"))
                .andExpect(jsonPath("$.mensaje", containsString("conjuntamente")));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - 400 Bad Request por dimension mayor a 300 cm")
    void testCotizacionDimensionExcesivaRetorna400() throws Exception {
        CotizacionRequest reqLadoGrande = new CotizacionRequest(
                servicioExpress.getId(), sucursalStgo.getId(), 8101, new BigDecimal("2.0"), 350, 20, 20
        );

        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqLadoGrande)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PARAMETROS_INVALIDOS"));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - 400 Bad Request por comuna de destino inexistente")
    void testCotizacionComunaInexistenteRetorna400() throws Exception {
        CotizacionRequest reqComunaInvalida = new CotizacionRequest(
                servicioExpress.getId(), sucursalStgo.getId(), 99901, new BigDecimal("2.0"), null, null, null
        );

        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqComunaInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PARAMETROS_INVALIDOS"));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - 422 Unprocessable Entity con codigo SERVICIO_INACTIVO")
    void testCotizacionServicioInactivoRetorna422() throws Exception {
        CotizacionRequest req = new CotizacionRequest(
                servicioInactivo.getId(), sucursalStgo.getId(), 8101, new BigDecimal("2.0"), null, null, null
        );

        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("SERVICIO_INACTIVO"));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - 422 Unprocessable Entity con codigo COMUNA_NO_CUBIERTA")
    void testCotizacionComunaExcluidaRetorna422() throws Exception {
        CotizacionRequest req = new CotizacionRequest(
                servicioExpress.getId(), sucursalStgo.getId(), 5201, new BigDecimal("2.0"), null, null, null
        );

        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("COMUNA_NO_CUBIERTA"));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - 422 Unprocessable Entity con codigo PESO_EXCEDE_LIMITE")
    void testCotizacionPesoExcedeLimiteRetorna422() throws Exception {
        CotizacionRequest req = new CotizacionRequest(
                servicioExpress.getId(), sucursalStgo.getId(), 8101, new BigDecimal("18.00"), null, null, null
        );

        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("PESO_EXCEDE_LIMITE"))
                .andExpect(jsonPath("$.limite").value(15.00));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - 422 Unprocessable Entity con codigo DIMENSION_EXCEDE_LIMITE")
    void testCotizacionDimensionExcedeLimiteRetorna422() throws Exception {
        // Lado mayor = 70 cm > limite 60 cm
        CotizacionRequest req = new CotizacionRequest(
                servicioExpress.getId(), sucursalStgo.getId(), 8101, new BigDecimal("2.0"), 70, 20, 20
        );

        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("DIMENSION_EXCEDE_LIMITE"))
                .andExpect(jsonPath("$.limite").value(60));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - Sucursal inactiva con validarSucursalActiva=true responde 422 SUCURSAL_INACTIVA")
    void testCotizacionSucursalInactivaValidacionActiva() throws Exception {
        CotizacionRequest req = new CotizacionRequest(
                servicioExpress.getId(),
                sucursalInactiva.getId(),
                8101,
                new BigDecimal("2.0"),
                null, null, null,
                true
        );

        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("SUCURSAL_INACTIVA"));
    }

    @Test
    @DisplayName("POST /api/catalogo/cotizaciones - Sucursal inactiva con validarSucursalActiva=false responde 200 OK (Caso E5 / D6)")
    void testCotizacionSucursalInactivaSinValidacionActiva() throws Exception {
        CotizacionRequest req = new CotizacionRequest(
                servicioExpress.getId(),
                sucursalInactiva.getId(),
                8101,
                new BigDecimal("2.0"),
                null, null, null,
                false
        );

        mockMvc.perform(post("/api/catalogo/cotizaciones")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicioCodigo").value("EXPRESS"))
                .andExpect(jsonPath("$.zona").value("INTERREGIONAL"));
    }
}
