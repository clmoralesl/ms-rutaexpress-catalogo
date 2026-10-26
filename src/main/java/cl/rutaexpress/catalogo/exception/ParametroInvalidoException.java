package cl.rutaexpress.catalogo.exception;

public class ParametroInvalidoException extends RuntimeException {
    public ParametroInvalidoException(String mensaje) {
        super(mensaje);
    }
}
