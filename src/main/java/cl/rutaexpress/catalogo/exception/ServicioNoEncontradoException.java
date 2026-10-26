package cl.rutaexpress.catalogo.exception;

public class ServicioNoEncontradoException extends RuntimeException {
    public ServicioNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public ServicioNoEncontradoException(Long id) {
        super("Servicio con ID " + id + " no encontrado");
    }
}
