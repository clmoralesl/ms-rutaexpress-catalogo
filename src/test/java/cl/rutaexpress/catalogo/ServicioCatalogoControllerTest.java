package cl.rutaexpress.catalogo;

import cl.rutaexpress.catalogo.dto.ActualizarServicioRequest;
import cl.rutaexpress.catalogo.dto.CrearServicioRequest;
import cl.rutaexpress.catalogo.entity.ServicioCatalogo;
import cl.rutaexpress.catalogo.repository.ServicioCatalogoRepository;
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
class ServicioCatalogoControllerTest {

    private static final String SECRETO_TEST = "test-secreto-interno-catalogo-seguro-32caracteres";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ServicioCatalogoRepository repository;

    private ServicioCatalogo servicioExpress;
    private ServicioCatalogo servicioAgotado;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        servicioExpress = new ServicioCatalogo(
                "EXPRESS",
                "Envio Express 24 Horas",
                "Entrega ultra rapida",
                "MOTO",
                new BigDecimal("3990.00"),
                new BigDecimal("890.00"),
                50
        );
        servicioExpress = repository.save(servicioExpress);

        servicioAgotado = new ServicioCatalogo(
                "MISMO_DIA",
                "Entrega Mismo Dia",
                "Same day delivery",
                "MOTO",
                new BigDecimal("5490.00"),
                new BigDecimal("1190.00"),
                10
        );
        servicioAgotado.setCapacidadDisponible(0); // Sin cupos
        servicioAgotado = repository.save(servicioAgotado);
    }

    @Test
    @DisplayName("GET /api/publico/salud - Debe responder 200 OK con estado UP sin cabecera de secreto")
    void testSaludPublica() throws Exception {
        mockMvc.perform(get("/api/publico/salud"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("UP"))
                .andExpect(jsonPath("$.servicio").value("ms-rutaexpress-catalogo"));
    }

    @Test
    @DisplayName("GET /actuator/health - Debe responder 200 OK con status UP sin cabecera de secreto")
    void testSaludActuator() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.servicio").value("ms-rutaexpress-catalogo"));
    }

    @Test
    @DisplayName("GET /api/catalogo/servicios - Debe retornar lista de servicios activos con cabecera interna")
    void testListarServicios() throws Exception {
        mockMvc.perform(get("/api/catalogo/servicios")
                        .header("X-Servicio-Interno", SECRETO_TEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].codigo").value("EXPRESS"))
                .andExpect(jsonPath("$[0].capacidadDisponible").value(50));
    }

    @Test
    @DisplayName("GET /api/catalogo/servicios/{id} - Debe retornar servicio por ID con cabecera interna")
    void testObtenerPorId() throws Exception {
        mockMvc.perform(get("/api/catalogo/servicios/" + servicioExpress.getId())
                        .header("X-Servicio-Interno", SECRETO_TEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("EXPRESS"))
                .andExpect(jsonPath("$.nombre").value("Envio Express 24 Horas"));
    }

    @Test
    @DisplayName("POST /api/catalogo/servicios - Debe crear un nuevo servicio correctamente")
    void testCrearServicio() throws Exception {
        CrearServicioRequest request = new CrearServicioRequest(
                "CARGA_PESADA",
                "Transporte Carga Pesada",
                "Para bultos de gran tamano",
                "CAMION",
                new BigDecimal("14990.00"),
                new BigDecimal("350.00"),
                15
        );

        mockMvc.perform(post("/api/catalogo/servicios")
                        .header("X-Servicio-Interno", SECRETO_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("CARGA_PESADA"))
                .andExpect(jsonPath("$.capacidadDisponible").value(15));
    }

    @Test
    @DisplayName("PUT /api/catalogo/servicios/{id} - Debe actualizar tarifas y capacidad")
    void testActualizarServicio() throws Exception {
        ActualizarServicioRequest request = new ActualizarServicioRequest(
                "Envio Express Actualizado",
                "Nueva descripcion",
                "FURGON",
                new BigDecimal("4500.00"),
                new BigDecimal("950.00"),
                60,
                true
        );

        mockMvc.perform(put("/api/catalogo/servicios/" + servicioExpress.getId())
                        .header("X-Servicio-Interno", SECRETO_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Envio Express Actualizado"))
                .andExpect(jsonPath("$.capacidadMaximaDiaria").value(60));
    }

    @Test
    @DisplayName("PATCH /api/catalogo/servicios/{id}/descontar-capacidad - Reduce cupo exitosamente")
    void testDescontarCapacidadExitoso() throws Exception {
        mockMvc.perform(patch("/api/catalogo/servicios/" + servicioExpress.getId() + "/descontar-capacidad")
                        .header("X-Servicio-Interno", SECRETO_TEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacidadDisponible").value(49));
    }

    @Test
    @DisplayName("PATCH /api/catalogo/servicios/{id}/descontar-capacidad - Rechaza con 409 Conflict cuando capacidad es 0")
    void testDescontarCapacidadAgotada() throws Exception {
        mockMvc.perform(patch("/api/catalogo/servicios/" + servicioAgotado.getId() + "/descontar-capacidad")
                        .header("X-Servicio-Interno", SECRETO_TEST))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICTO_CAPACIDAD"))
                .andExpect(jsonPath("$.mensaje", containsString("Capacidad de flota agotada")));
    }

    @Test
    @DisplayName("PATCH /api/catalogo/servicios/{id}/descontar-capacidad - Retorna 404 si el servicio no existe")
    void testDescontarCapacidadNoEncontrado() throws Exception {
        mockMvc.perform(patch("/api/catalogo/servicios/99999/descontar-capacidad")
                        .header("X-Servicio-Interno", SECRETO_TEST))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
