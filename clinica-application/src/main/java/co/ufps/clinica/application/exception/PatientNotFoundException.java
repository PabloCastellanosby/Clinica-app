package co.ufps.clinica.application.exception;
import java.util.UUID;
public final class PatientNotFoundException extends ApplicationException {
    private static final long serialVersionUID = 1L;
    public PatientNotFoundException(UUID id) { super("Paciente no encontrado: " + id); }
}
