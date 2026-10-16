package cl.rutaexpress.catalogo.exception;

public class CodigoDuplicadoException extends RuntimeException {
    public CodigoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
