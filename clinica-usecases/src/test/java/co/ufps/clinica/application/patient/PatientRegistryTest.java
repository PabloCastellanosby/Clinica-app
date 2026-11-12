package co.ufps.clinica.application.patient;

import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.patient.Patient;
import co.ufps.clinica.domain.patient.PatientId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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
