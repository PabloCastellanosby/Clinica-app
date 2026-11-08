package co.ufps.clinica.application;

import co.ufps.clinica.domain.ClinicModel.Patient;
import co.ufps.clinica.domain.PatientRepository;
import co.ufps.clinica.domain.exception.PatientAlreadyExistsException;
import co.ufps.clinica.application.exception.PatientNotFoundException;
import java.util.*;

/** Casos de uso de alta, edición y búsqueda; el repositorio debe imponer índice único documental. */
public final class PatientRegistry {
    private final PatientRepository repository;
    public PatientRegistry(PatientRepository repository) { this.repository = Objects.requireNonNull(repository); }
    public Patient register(Patient patient) {
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
    public List<Patient> search(String query) { return repository.search(query == null ? "" : query.trim()); }
}
