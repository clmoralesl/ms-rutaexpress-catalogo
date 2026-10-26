package cl.rutaexpress.catalogo.controller;

import cl.rutaexpress.catalogo.dto.ActualizarTarifaZonaRequest;
import cl.rutaexpress.catalogo.entity.TarifaZona;
import cl.rutaexpress.catalogo.entity.ZonaTarifa;
import cl.rutaexpress.catalogo.repository.TarifaZonaRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TarifaZonaControllerTest {

    private static final String SECRETO = "test-secreto-interno-catalogo-seguro-32caracteres";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TarifaZonaRepository tarifaZonaRepository;

    @BeforeEach
    void setUp() {
        tarifaZonaRepository.deleteAll();

        tarifaZonaRepository.save(new TarifaZona(ZonaTarifa.MISMA_COMUNA, new BigDecimal("1.00"), "Misma comuna"));
        tarifaZonaRepository.save(new TarifaZona(ZonaTarifa.MISMA_REGION, new BigDecimal("1.15"), "Misma region"));
        tarifaZonaRepository.save(new TarifaZona(ZonaTarifa.REGION_LIMITROFE, new BigDecimal("1.35"), "Region limitrofe"));
        tarifaZonaRepository.save(new TarifaZona(ZonaTarifa.INTERREGIONAL, new BigDecimal("1.70"), "Interregional"));
        tarifaZonaRepository.save(new TarifaZona(ZonaTarifa.EXTREMA, new BigDecimal("2.20"), "Extrema"));
    }

    @Test
    @DisplayName("GET /api/catalogo/tarifas-zonas - Lista los 5 factores de zona")
    void testListarTarifasZonas() throws Exception {
        mockMvc.perform(get("/api/catalogo/tarifas-zonas")
                        .header("X-Servicio-Interno", SECRETO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    @DisplayName("GET /api/catalogo/tarifas-zonas/{zona} - Obtiene factor de zona especifica")
    void testObtenerPorZona() throws Exception {
        mockMvc.perform(get("/api/catalogo/tarifas-zonas/EXTREMA")
                        .header("X-Servicio-Interno", SECRETO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zona").value("EXTREMA"))
                .andExpect(jsonPath("$.factor").value(2.20));
    }

    @Test
    @DisplayName("GET /api/catalogo/tarifas-zonas/{zona} - 400 Bad Request ante zona invalida (E-A5)")
    void testObtenerPorZonaInvalida() throws Exception {
        mockMvc.perform(get("/api/catalogo/tarifas-zonas/FOO")
                        .header("X-Servicio-Interno", SECRETO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("SOLICITUD_INVALIDA"));
    }

    @Test
    @DisplayName("PUT /api/catalogo/tarifas-zonas/{zona} - Actualiza factor correctamente")
    void testActualizarFactor() throws Exception {
        ActualizarTarifaZonaRequest req = new ActualizarTarifaZonaRequest(
                new BigDecimal("2.50"),
                "Ajuste por costos de combustible en zonas extremas"
        );

        mockMvc.perform(put("/api/catalogo/tarifas-zonas/EXTREMA")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zona").value("EXTREMA"))
                .andExpect(jsonPath("$.factor").value(2.50))
                .andExpect(jsonPath("$.descripcion").value("Ajuste por costos de combustible en zonas extremas"));
    }

    @Test
    @DisplayName("PUT /api/catalogo/tarifas-zonas/{zona} - 400 Bad Request si factor es menor a 1.00")
    void testActualizarFactorMenorAUno() throws Exception {
        ActualizarTarifaZonaRequest reqInvalido = new ActualizarTarifaZonaRequest(
                new BigDecimal("0.80"),
                "Factor menor a 1.00"
        );

        mockMvc.perform(put("/api/catalogo/tarifas-zonas/EXTREMA")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("DATOS_INVALIDOS"));
    }

    @Test
    @DisplayName("PUT /api/catalogo/tarifas-zonas/{zona} - 400 Bad Request si factor excede 5.00 (5.01 y 120)")
    void testActualizarFactorExcedeMaximo() throws Exception {
        ActualizarTarifaZonaRequest req501 = new ActualizarTarifaZonaRequest(
                new BigDecimal("5.01"),
                "Factor excede 5.00"
        );

        mockMvc.perform(put("/api/catalogo/tarifas-zonas/EXTREMA")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req501)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("DATOS_INVALIDOS"));

        ActualizarTarifaZonaRequest req120 = new ActualizarTarifaZonaRequest(
                new BigDecimal("120.00"),
                "Factor 120"
        );

        mockMvc.perform(put("/api/catalogo/tarifas-zonas/EXTREMA")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req120)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("DATOS_INVALIDOS"));
    }

    @Test
    @DisplayName("PUT /api/catalogo/tarifas-zonas/{zona} - 400 Bad Request si factor tiene mas de 2 decimales (1.255)")
    void testActualizarFactorDecimalesInvalidos() throws Exception {
        ActualizarTarifaZonaRequest req1255 = new ActualizarTarifaZonaRequest(
                new BigDecimal("1.255"),
                "Factor con 3 decimales"
        );

        mockMvc.perform(put("/api/catalogo/tarifas-zonas/EXTREMA")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req1255)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("DATOS_INVALIDOS"));
    }
}
