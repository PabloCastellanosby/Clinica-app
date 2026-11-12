package co.ufps.clinica.application.patient;
import co.ufps.clinica.application.exception.ApplicationException;
import co.ufps.clinica.domain.patient.PatientId;
public final class PatientNotFoundException extends ApplicationException {
    private static final long serialVersionUID = 1L;
    public PatientNotFoundException(PatientId id) { super("Paciente no encontrado: " + id.value()); }
    public PatientNotFoundException() { super("No existe un perfil de paciente asociado a la cuenta"); }
}
