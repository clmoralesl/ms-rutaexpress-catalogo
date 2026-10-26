package cl.rutaexpress.catalogo.controller;

import cl.rutaexpress.catalogo.dto.SucursalRequest;
import cl.rutaexpress.catalogo.entity.GeoRegion;
import cl.rutaexpress.catalogo.entity.Sucursal;
import cl.rutaexpress.catalogo.repository.GeoRegionRepository;
import cl.rutaexpress.catalogo.repository.SucursalRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SucursalControllerTest {

    private static final String SECRETO = "test-secreto-interno-catalogo-seguro-32caracteres";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private GeoRegionRepository geoRegionRepository;

    private Sucursal sucursalActiva;
    private Sucursal sucursalInactiva;

    @BeforeEach
    void setUp() {
        sucursalRepository.deleteAll();
        geoRegionRepository.deleteAll();

        geoRegionRepository.save(new GeoRegion(5, "Valparaíso", false));
        geoRegionRepository.save(new GeoRegion(13, "Metropolitana", false));

        sucursalActiva = sucursalRepository.save(new Sucursal(
                "SUC-STGO", "Santiago Centro", "Moneda", "1122", "Of. 101", 13101, "09:00-19:00", true
        ));

        sucursalInactiva = sucursalRepository.save(new Sucursal(
                "SUC-CERRADA", "Sucursal Temporal Cerrada", "Alameda", "500", null, 13101, "Cerrada", false
        ));
    }

    @Test
    @DisplayName("GET /api/catalogo/sucursales - Lista todas las sucursales")
    void testListarTodas() throws Exception {
        mockMvc.perform(get("/api/catalogo/sucursales")
                        .header("X-Servicio-Interno", SECRETO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/catalogo/sucursales?activas=true - Lista unicamente sucursales activas")
    void testListarSoloActivas() throws Exception {
        mockMvc.perform(get("/api/catalogo/sucursales?activas=true")
                        .header("X-Servicio-Interno", SECRETO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigo").value("SUC-STGO"));
    }

    @Test
    @DisplayName("GET /api/catalogo/sucursales/{id} - Obtener por ID existente y 404 para inexistente")
    void testObtenerPorId() throws Exception {
        mockMvc.perform(get("/api/catalogo/sucursales/" + sucursalActiva.getId())
                        .header("X-Servicio-Interno", SECRETO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("SUC-STGO"))
                .andExpect(jsonPath("$.nombre").value("Santiago Centro"));

        mockMvc.perform(get("/api/catalogo/sucursales/999999")
                        .header("X-Servicio-Interno", SECRETO))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("SUCURSAL_NO_ENCONTRADA"));
    }

    @Test
    @DisplayName("POST /api/catalogo/sucursales - Crear sucursal nueva y 400 ante codigo duplicado")
    void testCrearSucursal() throws Exception {
        SucursalRequest nueva = new SucursalRequest(
                "SUC-VALP", "Valparaiso", "Prat", "780", null, 5101, "09:00-18:00", true
        );

        mockMvc.perform(post("/api/catalogo/sucursales")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("SUC-VALP"))
                .andExpect(jsonPath("$.idComuna").value(5101));

        // Intento de duplicado
        mockMvc.perform(post("/api/catalogo/sucursales")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("CODIGO_DUPLICADO"));
    }

    @Test
    @DisplayName("POST /api/catalogo/sucursales - 400 Bad Request ante comuna invalida (M2)")
    void testCrearSucursalComunaInvalida() throws Exception {
        SucursalRequest nueva = new SucursalRequest(
                "SUC-INV", "Invalida", "Calle", "100", null, 99999, "09:00-18:00", true
        );

        mockMvc.perform(post("/api/catalogo/sucursales")
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nueva)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("PARAMETROS_INVALIDOS"));
    }

    @Test
    @DisplayName("PUT /api/catalogo/sucursales/{id} - Actualizar sucursal existente")
    void testActualizarSucursal() throws Exception {
        SucursalRequest update = new SucursalRequest(
                "SUC-STGO", "Santiago Centro Renovado", "Moneda", "1150", "Piso 2", 13101, "08:30-19:30", true
        );

        mockMvc.perform(put("/api/catalogo/sucursales/" + sucursalActiva.getId())
                        .header("X-Servicio-Interno", SECRETO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Santiago Centro Renovado"))
                .andExpect(jsonPath("$.numero").value("1150"));
    }
}
