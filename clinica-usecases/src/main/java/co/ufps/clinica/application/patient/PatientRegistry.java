package co.ufps.clinica.application.patient;

import co.ufps.clinica.domain.patient.Patient;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.domain.patient.PatientAlreadyExistsException;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.identity.UserId;
import java.time.LocalDate;
import java.util.*;

/** Casos de uso de alta, edición y búsqueda; el repositorio debe imponer índice único documental. */
public final class PatientRegistry {
    private final PatientRepository repository;
    public PatientRegistry(PatientRepository repository) { this.repository = Objects.requireNonNull(repository); }
    public Patient register(RegisterPatientCommand command) {
        RegisterPatientCommand valid = validate(command);
        Patient patient = Patient.register(valid.documentType(), valid.documentNumber(), valid.firstName(), valid.lastName(),
                valid.birthDate(), valid.email(), valid.phone(), valid.address(), valid.affiliationType());
        if (repository.findByDocument(patient.documentType(), patient.documentNumber()).isPresent()) throw new PatientAlreadyExistsException(patient.documentNumber());
        return repository.save(patient);
    }
    public Patient update(Patient patient) {
        patient = normalize(patient);
        if (repository.findById(patient.id()).isEmpty()) throw new PatientNotFoundException(patient.id());
        Optional<Patient> duplicate = repository.findByDocument(patient.documentType(), patient.documentNumber());
        if (duplicate.isPresent() && !duplicate.get().id().equals(patient.id())) throw new PatientAlreadyExistsException(patient.documentNumber());
        return repository.save(patient);
    }
    public Patient findById(PatientId id) {
        return repository.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
    }
    public Patient profileForUser(UserId userId) {
        return repository.findByUserId(userId).orElseThrow(PatientNotFoundException::new);
    }
    public Patient updateProfile(PatientId id, RegisterPatientCommand command) {
        Patient current = findById(id);
        RegisterPatientCommand valid = validate(command);
        Patient updated = new Patient(current.id(), valid.documentType(), valid.documentNumber(), valid.firstName(),
                valid.lastName(), valid.birthDate(), valid.email(), valid.phone(), valid.address(),
                valid.affiliationType(), current.createdAt(), current.accountUserId());
        return update(updated);
    }
    public List<Patient> search(String query) {
        if (query == null || query.isBlank()) return List.of();
        String term = query.trim();
        if (term.length() > 120) throw new IllegalArgumentException("El criterio de búsqueda supera 120 caracteres");
        return repository.search(term);
    }

    private static RegisterPatientCommand validate(RegisterPatientCommand command) {
        if (command == null) throw new IllegalArgumentException("Los datos del paciente son obligatorios");
        String documentType = required(command.documentType(), "El tipo de documento", 40).toUpperCase(Locale.ROOT);
        String documentNumber = required(command.documentNumber(), "El documento", 80).toUpperCase(Locale.ROOT);
        String firstName = required(command.firstName(), "El nombre", 120);
        String lastName = required(command.lastName(), "Los apellidos", 120);
        if (command.birthDate() != null && command.birthDate().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        String email = optional(command.email(), "El correo", 254);
        if (email != null && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new IllegalArgumentException("El correo del paciente no tiene un formato válido");
        if (email != null) email = email.toLowerCase(Locale.ROOT);
        return new RegisterPatientCommand(documentType, documentNumber, firstName, lastName, command.birthDate(),
                email, optional(command.phone(), "El teléfono", 40), optional(command.address(), "La dirección", 500),
                optional(command.affiliationType(), "El tipo de afiliación", 80));
    }

    private static Patient normalize(Patient patient) {
        if (patient == null || patient.id() == null || patient.createdAt() == null)
            throw new IllegalArgumentException("El paciente debe tener identificador y fecha de registro");
        RegisterPatientCommand valid = validate(new RegisterPatientCommand(patient.documentType(), patient.documentNumber(),
                patient.firstName(), patient.lastName(), patient.birthDate(), patient.email(), patient.phone(),
                patient.address(), patient.affiliationType()));
        return new Patient(patient.id(), valid.documentType(), valid.documentNumber(), valid.firstName(), valid.lastName(),
                valid.birthDate(), valid.email(), valid.phone(), valid.address(), valid.affiliationType(),
                patient.createdAt(), patient.accountUserId());
    }

    private static String required(String value, String field, int maxLength) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty() || normalized.length() > maxLength)
            throw new IllegalArgumentException(field + " es obligatorio y no puede superar " + maxLength + " caracteres");
        return normalized;
    }

    private static String optional(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) return null;
        String normalized = value.trim();
        if (normalized.length() > maxLength)
            throw new IllegalArgumentException(field + " no puede superar " + maxLength + " caracteres");
        return normalized;
    }
}
