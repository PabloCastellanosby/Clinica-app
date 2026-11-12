package co.ufps.clinica.application.identity;

public final class InvalidCredentialsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}
