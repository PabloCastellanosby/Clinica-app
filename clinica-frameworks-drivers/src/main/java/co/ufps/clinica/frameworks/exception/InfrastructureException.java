package co.ufps.clinica.frameworks.exception;
public class InfrastructureException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public InfrastructureException(String message, Throwable cause) { super(message, cause); }
}
