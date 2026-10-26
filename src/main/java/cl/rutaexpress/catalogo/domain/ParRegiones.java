package cl.rutaexpress.catalogo.domain;

/**
 * Representa un par no ordenado de identificadores de regiones limitrofes.
 * Se normaliza internamente con regionA <= regionB para garantizar igualdad reflexiva en comparaciones.
 */
public record ParRegiones(int regionA, int regionB) {

    public ParRegiones {
        if (regionA > regionB) {
            int temp = regionA;
            regionA = regionB;
            regionB = temp;
        }
    }

    public static ParRegiones de(int a, int b) {
        return new ParRegiones(Math.min(a, b), Math.max(a, b));
    }
}
