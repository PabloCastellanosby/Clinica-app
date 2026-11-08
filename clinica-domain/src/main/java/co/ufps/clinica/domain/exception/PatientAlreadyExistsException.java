package co.ufps.clinica.domain.exception;
public final class PatientAlreadyExistsException extends DomainException {
    private static final long serialVersionUID = 1L;
    public PatientAlreadyExistsException(String document) { super("Ya existe un paciente con el documento " + document); }
}
