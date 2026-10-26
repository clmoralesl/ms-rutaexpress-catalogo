package cl.rutaexpress.catalogo.exception;

public class SucursalNoEncontradaException extends RuntimeException {
    public SucursalNoEncontradaException(Long id) {
        super("Sucursal no encontrada con id: " + id);
    }
    public SucursalNoEncontradaException(String codigo) {
        super("Sucursal no encontrada con codigo: " + codigo);
    }
}
