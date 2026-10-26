package cl.rutaexpress.catalogo.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Motor matematico puro para el calculo de tarifas logisticas.
 * Libre de dependencias con frameworks para ejecucion y pruebas desacopladas.
 */
public final class CalculadoraTarifa {

    private static final BigDecimal CERO = BigDecimal.ZERO;

    private CalculadoraTarifa() {
        // Constructor privado para clase utilitaria
    }

    public record Entrada(
            BigDecimal tarifaBase,
            BigDecimal precioPorKg,
            BigDecimal factorZona,
            BigDecimal pesoKg,
            Integer largoCm,
            Integer anchoCm,
            Integer altoCm,
            int divisorVolumetrico
    ) {}

    public record Resultado(
            BigDecimal pesoVolumetricoKg,
            BigDecimal pesoFacturableKg,
            BigDecimal subtotal,
            long totalClp
    ) {}

    public static Resultado calcular(Entrada e) {
        BigDecimal volumetrico = CERO;
        if (e.largoCm() != null && e.anchoCm() != null && e.altoCm() != null) {
            volumetrico = BigDecimal.valueOf((long) e.largoCm() * e.anchoCm() * e.altoCm())
                    .divide(BigDecimal.valueOf(e.divisorVolumetrico()), 4, RoundingMode.HALF_UP);
        }

        BigDecimal facturable = e.pesoKg().max(volumetrico).setScale(1, RoundingMode.CEILING);
        BigDecimal subtotal = e.tarifaBase().add(facturable.multiply(e.precioPorKg())).setScale(2, RoundingMode.HALF_UP);
        long total = subtotal.multiply(e.factorZona()).setScale(0, RoundingMode.HALF_UP).longValueExact();

        return new Resultado(volumetrico, facturable, subtotal, total);
    }
}
