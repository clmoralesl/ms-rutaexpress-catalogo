package cl.rutaexpress.catalogo.exception;

public class ServicioNoEncontradoException extends RuntimeException {
    public ServicioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
