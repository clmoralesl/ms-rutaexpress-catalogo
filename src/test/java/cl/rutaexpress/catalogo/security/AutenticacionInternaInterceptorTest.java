package cl.rutaexpress.catalogo.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutenticacionInternaInterceptorTest {

    private static final String SECRETO_VALIDO = "test-secreto-interno-catalogo-seguro-32caracteres";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Peticion sin cabecera X-Servicio-Interno responde 401 Unauthorized")
    void testPeticionSinCabeceraRetorna401() throws Exception {
        mockMvc.perform(get("/api/catalogo/servicios"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Acceso no autorizado: falta cabecera X-Servicio-Interno"));
    }

    @Test
    @DisplayName("Peticion con cabecera X-Servicio-Interno incorrecta responde 401 Unauthorized")
    void testPeticionConSecretoInvalidoRetorna401() throws Exception {
        mockMvc.perform(get("/api/catalogo/servicios")
                        .header("X-Servicio-Interno", "clave-invalida-incorrecta-32caracteres"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Acceso no autorizado: credencial interna invalida"));
    }

    @Test
    @DisplayName("Peticion con cabecera X-Servicio-Interno valida responde 200 OK")
    void testPeticionConSecretoValidoRetorna200() throws Exception {
        mockMvc.perform(get("/api/catalogo/servicios")
                        .header("X-Servicio-Interno", SECRETO_VALIDO))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Rutas publicas no exigen cabecera interna")
    void testRutasPublicasSinCabecera() throws Exception {
        mockMvc.perform(get("/api/publico/salud"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Validacion @PostConstruct rechaza secreto nulo, corto o con marcadores")
    void testValidacionPostConstruct() {
        assertThrows(IllegalStateException.class, () -> new AutenticacionInternaInterceptor(null).validarConfiguracionSecreto());
        assertThrows(IllegalStateException.class, () -> new AutenticacionInternaInterceptor("").validarConfiguracionSecreto());
        assertThrows(IllegalStateException.class, () -> new AutenticacionInternaInterceptor("corto").validarConfiguracionSecreto());
        assertThrows(IllegalStateException.class, () -> new AutenticacionInternaInterceptor("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx").validarConfiguracionSecreto());
        assertThrows(IllegalStateException.class, () -> new AutenticacionInternaInterceptor("changeme").validarConfiguracionSecreto());
    }
}
