package cl.rutaexpress.catalogo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraTarifaTest {

    @Test
    @DisplayName("Caso de Referencia Dictamen: (3500 + 6.0 x 900) x 1.70 = 8900 x 1.70 = 15130 CLP")
    void testCasoReferenciaDictamen() {
        CalculadoraTarifa.Entrada entrada = new CalculadoraTarifa.Entrada(
                new BigDecimal("3500.00"),
                new BigDecimal("900.00"),
                new BigDecimal("1.70"),
                new BigDecimal("3.20"),
                40,
                30,
                20,
                4000
        );

        CalculadoraTarifa.Resultado res = CalculadoraTarifa.calcular(entrada);

        // Volumetrico = 40 * 30 * 20 / 4000 = 6.0000 kg
        assertEquals(new BigDecimal("6.0000"), res.pesoVolumetricoKg());
        // Facturable = max(3.20, 6.0) = 6.0 kg
        assertEquals(new BigDecimal("6.0"), res.pesoFacturableKg());
        // Subtotal = 3500 + 6.0 * 900 = 8900.00
        assertEquals(new BigDecimal("8900.00"), res.subtotal());
        // Total = 8900 * 1.70 = 15130 CLP
        assertEquals(15130L, res.totalClp());
    }

    @Test
    @DisplayName("Peso real mayor que peso volumetrico")
    void testRealMayorQueVolumetrico() {
        CalculadoraTarifa.Entrada entrada = new CalculadoraTarifa.Entrada(
                new BigDecimal("2000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("1.00"),
                new BigDecimal("8.50"),
                20,
                20,
                20,
                4000
        );

        CalculadoraTarifa.Resultado res = CalculadoraTarifa.calcular(entrada);

        // Volumetrico = 8000 / 4000 = 2.0000 kg
        assertEquals(new BigDecimal("2.0000"), res.pesoVolumetricoKg());
        // Facturable = max(8.50, 2.0) = 8.5 kg
        assertEquals(new BigDecimal("8.5"), res.pesoFacturableKg());
        // Subtotal = 2000 + 8.5 * 500 = 6250.00
        assertEquals(new BigDecimal("6250.00"), res.subtotal());
        assertEquals(6250L, res.totalClp());
    }

    @Test
    @DisplayName("Calculo sin dimensiones: volumetrico es 0 y se factura el peso real")
    void testSinDimensiones() {
        CalculadoraTarifa.Entrada entrada = new CalculadoraTarifa.Entrada(
                new BigDecimal("1500.00"),
                new BigDecimal("400.00"),
                new BigDecimal("1.15"),
                new BigDecimal("4.00"),
                null,
                null,
                null,
                4000
        );

        CalculadoraTarifa.Resultado res = CalculadoraTarifa.calcular(entrada);

        assertEquals(BigDecimal.ZERO, res.pesoVolumetricoKg());
        assertEquals(new BigDecimal("4.0"), res.pesoFacturableKg());
        // Subtotal = 1500 + 4.0 * 400 = 3100.00
        // Total = 3100 * 1.15 = 3565 CLP
        assertEquals(3565L, res.totalClp());
    }

    @Test
    @DisplayName("Redondeo CEILING a 0.1 kg en peso facturable")
    void testRedondeoCeilingPesoFacturable() {
        CalculadoraTarifa.Entrada entrada1 = new CalculadoraTarifa.Entrada(
                new BigDecimal("1000.00"),
                new BigDecimal("100.00"),
                new BigDecimal("1.00"),
                new BigDecimal("2.11"),
                null,
                null,
                null,
                4000
        );
        assertEquals(new BigDecimal("2.2"), CalculadoraTarifa.calcular(entrada1).pesoFacturableKg());

        CalculadoraTarifa.Entrada entrada2 = new CalculadoraTarifa.Entrada(
                new BigDecimal("1000.00"),
                new BigDecimal("100.00"),
                new BigDecimal("1.00"),
                new BigDecimal("2.10"),
                null,
                null,
                null,
                4000
        );
        assertEquals(new BigDecimal("2.1"), CalculadoraTarifa.calcular(entrada2).pesoFacturableKg());
    }

    @Test
    @DisplayName("Redondeo HALF_UP a pesos enteros en total CLP")
    void testRedondeoHalfUpPesosEnteros() {
        // Subtotal = 100, factor = 1.004 -> 100.4 -> 100
        CalculadoraTarifa.Entrada entradaAbajo = new CalculadoraTarifa.Entrada(
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                new BigDecimal("1.004"),
                new BigDecimal("1.0"),
                null,
                null,
                null,
                4000
        );
        assertEquals(100L, CalculadoraTarifa.calcular(entradaAbajo).totalClp());

        // Subtotal = 100, factor = 1.005 -> 100.5 -> 101
        CalculadoraTarifa.Entrada entradaArriba = new CalculadoraTarifa.Entrada(
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                new BigDecimal("1.005"),
                new BigDecimal("1.0"),
                null,
                null,
                null,
                4000
        );
        assertEquals(101L, CalculadoraTarifa.calcular(entradaArriba).totalClp());
    }
}
