package co.ufps.clinica.application.patient;

import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.patient.Patient;
import co.ufps.clinica.domain.patient.PatientId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PatientRegistryTest {
    @Test
    void emptySearchDoesNotQueryOrListAllPatients() {
        AtomicBoolean queried = new AtomicBoolean();
        PatientRegistry registry = new PatientRegistry(repository(queried));

        assertEquals(List.of(), registry.search("  "));
        assertFalse(queried.get());
    }

    @Test
    void capsSearchInputLength() {
        PatientRegistry registry = new PatientRegistry(repository(new AtomicBoolean()));
        assertThrows(IllegalArgumentException.class, () -> registry.search("a".repeat(121)));
    }

    @Test
    void validatesAndNormalizesPatientIdentityBeforeSaving() {
        AtomicReference<Patient> saved = new AtomicReference<>();
        PatientRepository patients = new PatientRepository() {
            public Optional<Patient> findByDocument(String type, String number) { return Optional.empty(); }
            public Optional<Patient> findById(PatientId id) { return Optional.empty(); }
            public Optional<Patient> findByUserId(UserId id) { return Optional.empty(); }
            public Patient save(Patient patient) { saved.set(patient); return patient; }
            public List<Patient> search(String query) { return List.of(); }
        };
        PatientRegistry registry = new PatientRegistry(patients);

        registry.register(new RegisterPatientCommand(" cc ", " ab-123 ", " Ana ", " Pérez ", null,
                " ANA@example.test ", null, null, null));

        assertEquals("CC", saved.get().documentType());
        assertEquals("AB-123", saved.get().documentNumber());
        assertEquals("Ana", saved.get().firstName());
        assertEquals("ana@example.test", saved.get().email());
        assertThrows(IllegalArgumentException.class, () -> registry.register(
                new RegisterPatientCommand("CC", "1", "", "Pérez", null, null, null, null, null)));
    }

    private PatientRepository repository(AtomicBoolean queried) {
        return new PatientRepository() {
            public Optional<Patient> findByDocument(String type, String number) { return Optional.empty(); }
            public Optional<Patient> findById(PatientId id) { return Optional.empty(); }
            public Optional<Patient> findByUserId(UserId id) { return Optional.empty(); }
            public Patient save(Patient patient) { return patient; }
            public List<Patient> search(String query) { queried.set(true); return List.of(); }
        };
    }
}
