package cl.rutaexpress.catalogo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ReglaCutComunasTest {

    @Test
    @DisplayName("Regla Matematica CUT: 346 de 346 comunas de Chile cumplen que id_comuna / 1000 == id_region")
    void testReglaCutSobreComunasOficiales() throws Exception {
        InputStream is = getClass().getResourceAsStream("/comunas_cut.csv");
        assertNotNull(is, "El recurso comunas_cut.csv debe existir en el classpath del catalogo");

        int comunasValidadas = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String linea = reader.readLine(); // Leer cabecera
            assertEquals("id_comuna,id_region", linea != null ? linea.trim() : null);

            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(",");
                assertEquals(2, partes.length);

                int idComuna = Integer.parseInt(partes[0].trim());
                int idRegion = Integer.parseInt(partes[1].trim());

                int regionCalculada = idComuna / 1000;
                assertEquals(idRegion, regionCalculada,
                        "La comuna " + idComuna + " no cumple la regla CUT (idRegion=" + idRegion + ", calculada=" + regionCalculada + ")");

                comunasValidadas++;
            }
        }

        assertEquals(346, comunasValidadas, "Deben haberse validado exactamente las 346 comunas oficiales de Chile");
    }
}
