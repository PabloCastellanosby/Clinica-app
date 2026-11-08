package co.ufps.clinica.application.exception;
import co.ufps.clinica.domain.PatientId;
public final class PatientNotFoundException extends ApplicationException {
    private static final long serialVersionUID = 1L;
    public PatientNotFoundException(PatientId id) { super("Paciente no encontrado: " + id.value()); }
}
