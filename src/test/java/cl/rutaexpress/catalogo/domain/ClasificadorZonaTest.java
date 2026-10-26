package cl.rutaexpress.catalogo.domain;

import cl.rutaexpress.catalogo.entity.ZonaTarifa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClasificadorZonaTest {

    private final Set<Integer> extremas = Set.of(15, 11, 12);
    private final Set<ParRegiones> limitrofes = Set.of(
            ParRegiones.de(1, 2),
            ParRegiones.de(1, 15),
            ParRegiones.de(2, 3),
            ParRegiones.de(3, 4),
            ParRegiones.de(4, 5),
            ParRegiones.de(5, 6),
            ParRegiones.de(5, 13),
            ParRegiones.de(6, 7),
            ParRegiones.de(6, 13),
            ParRegiones.de(7, 16),
            ParRegiones.de(8, 9),
            ParRegiones.de(8, 16),
            ParRegiones.de(9, 14),
            ParRegiones.de(10, 11),
            ParRegiones.de(10, 14),
            ParRegiones.de(11, 12)
    );

    @Test
    @DisplayName("Zona: MISMA_COMUNA cuando origen y destino tienen exactamente el mismo idComuna")
    void testMismaComuna() {
        ZonaTarifa zona = ClasificadorZona.clasificar(13101, 13101, extremas, limitrofes);
        assertEquals(ZonaTarifa.MISMA_COMUNA, zona);
    }

    @Test
    @DisplayName("Zona: MISMA_REGION cuando id_comuna / 1000 coincide y no es la misma comuna")
    void testMismaRegion() {
        // Santiago (13101) y Providencia (13123) -> Region 13
        ZonaTarifa zona = ClasificadorZona.clasificar(13101, 13123, extremas, limitrofes);
        assertEquals(ZonaTarifa.MISMA_REGION, zona);
    }

    @Test
    @DisplayName("Zona: EXTREMA cuando origen o destino pertenece a region extrema (15, 11 o 12)")
    void testZonaExtrema() {
        // Santiago (13) a Arica (15101 en region 15 extrema)
        ZonaTarifa zonaDestinoExtremo = ClasificadorZona.clasificar(13101, 15101, extremas, limitrofes);
        assertEquals(ZonaTarifa.EXTREMA, zonaDestinoExtremo);

        // Coyhaique (11101 en region 11 extrema) a Concepcion (8101)
        ZonaTarifa zonaOrigenExtremo = ClasificadorZona.clasificar(11101, 8101, extremas, limitrofes);
        assertEquals(ZonaTarifa.EXTREMA, zonaOrigenExtremo);

        // Ambas comunas en regiones extremas
        ZonaTarifa ambasExtremas = ClasificadorZona.clasificar(15101, 12101, extremas, limitrofes);
        assertEquals(ZonaTarifa.EXTREMA, ambasExtremas);
    }

    @Test
    @DisplayName("Zona: REGION_LIMITROFE evaluada reflexivamente en ambos sentidos")
    void testRegionLimitrofeBidireccional() {
        // Valparaiso (5101, region 5) y Santiago (13101, region 13)
        ZonaTarifa zona1 = ClasificadorZona.clasificar(5101, 13101, extremas, limitrofes);
        assertEquals(ZonaTarifa.REGION_LIMITROFE, zona1);

        ZonaTarifa zona2 = ClasificadorZona.clasificar(13101, 5101, extremas, limitrofes);
        assertEquals(ZonaTarifa.REGION_LIMITROFE, zona2);

        // Maule (7101, region 7) y Nuble (16101, region 16)
        ZonaTarifa zonaNuble = ClasificadorZona.clasificar(7101, 16101, extremas, limitrofes);
        assertEquals(ZonaTarifa.REGION_LIMITROFE, zonaNuble);
    }

    @Test
    @DisplayName("Zona: INTERREGIONAL para regiones no limitrofes y no extremas")
    void testInterregional() {
        // Antofagasta (2101, region 2) y Santiago (13101, region 13)
        ZonaTarifa zona = ClasificadorZona.clasificar(2101, 13101, extremas, limitrofes);
        assertEquals(ZonaTarifa.INTERREGIONAL, zona);
    }
}
