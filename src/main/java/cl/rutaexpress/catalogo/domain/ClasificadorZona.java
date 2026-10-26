package cl.rutaexpress.catalogo.domain;

import cl.rutaexpress.catalogo.entity.ZonaTarifa;

import java.util.Set;

/**
 * Motor puro de clasificacion geografica zonal.
 * Aplica la jerarquia estricta de zonas mediante el codigo CUT (idComuna / 1000 = idRegion).
 */
public final class ClasificadorZona {

    private ClasificadorZona() {
        // Constructor privado para clase utilitaria
    }

    public static ZonaTarifa clasificar(
            int comunaOrigen,
            int comunaDestino,
            Set<Integer> regionesExtremas,
            Set<ParRegiones> limitrofes) {

        int regionOrigen = comunaOrigen / 1000;
        int regionDestino = comunaDestino / 1000;

        // 1. Misma Comuna
        if (comunaOrigen == comunaDestino) {
            return ZonaTarifa.MISMA_COMUNA;
        }

        // 2. Misma Region
        if (regionOrigen == regionDestino) {
            return ZonaTarifa.MISMA_REGION;
        }

        // 3. Zona Extrema (origen o destino en region extrema)
        if (regionesExtremas != null && (regionesExtremas.contains(regionOrigen) || regionesExtremas.contains(regionDestino))) {
            return ZonaTarifa.EXTREMA;
        }

        // 4. Region Limitrofe
        if (limitrofes != null && limitrofes.contains(ParRegiones.de(regionOrigen, regionDestino))) {
            return ZonaTarifa.REGION_LIMITROFE;
        }

        // 5. Interregional (resto de casos)
        return ZonaTarifa.INTERREGIONAL;
    }
}
