package co.ufps.clinica.application.patient;

import co.ufps.clinica.domain.patient.Patient;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.domain.patient.PatientAlreadyExistsException;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.identity.UserId;
import java.util.*;

/** Casos de uso de alta, edición y búsqueda; el repositorio debe imponer índice único documental. */
public final class PatientRegistry {
    private final PatientRepository repository;
    public PatientRegistry(PatientRepository repository) { this.repository = Objects.requireNonNull(repository); }
    public Patient register(RegisterPatientCommand command) {
        Patient patient = Patient.register(command.documentType(), command.documentNumber(), command.firstName(), command.lastName(), command.birthDate(), command.email(), command.phone(), command.address(), command.affiliationType());
        if (patient.documentNumber() == null || patient.documentNumber().isBlank()) throw new IllegalArgumentException("El documento es obligatorio");
        if (repository.findByDocument(patient.documentType(), patient.documentNumber()).isPresent()) throw new PatientAlreadyExistsException(patient.documentNumber());
        return repository.save(patient);
    }
    public Patient update(Patient patient) {
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
        Patient updated = new Patient(current.id(), command.documentType(), command.documentNumber(), command.firstName(),
                command.lastName(), command.birthDate(), command.email(), command.phone(), command.address(),
                command.affiliationType(), current.createdAt(), current.accountUserId());
        return update(updated);
    }
    public List<Patient> search(String query) {
        if (query == null || query.isBlank()) return List.of();
        String term = query.trim();
        if (term.length() > 120) throw new IllegalArgumentException("El criterio de búsqueda supera 120 caracteres");
        return repository.search(term);
    }
}
