package cl.rutaexpress.catalogo.exception;

public class ReglaNegocioException extends RuntimeException {

    private final String codigo;
    private final Object limite;

    public ReglaNegocioException(String codigo, String mensaje, Object limite) {
        super(mensaje);
        this.codigo = codigo;
        this.limite = limite;
    }

    public ReglaNegocioException(String codigo, String mensaje) {
        this(codigo, mensaje, null);
    }

    public String getCodigo() {
        return codigo;
    }

    public Object getLimite() {
        return limite;
    }
}
