package cl.rutaexpress.catalogo.exception;

public class CapacidadAgotadaException extends RuntimeException {
    public CapacidadAgotadaException(String mensaje) {
        super(mensaje);
    }
}
