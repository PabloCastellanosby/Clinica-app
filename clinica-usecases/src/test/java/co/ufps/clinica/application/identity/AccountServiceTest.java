package co.ufps.clinica.application.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.ufps.clinica.application.port.out.identity.UserRepository;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.domain.identity.Role;
import co.ufps.clinica.domain.identity.User;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.patient.Patient;
import co.ufps.clinica.domain.patient.PatientId;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class AccountServiceTest {
    @Test void patientAccountRegistrationNormalizesDocumentLikePatientRegistry() {
        Patient existing = Patient.register("CC", "AB-123", "Ana", "Pérez", null,
                "ana@example.test", null, null, null);
        AtomicReference<String> lookup = new AtomicReference<>();
        AtomicReference<Patient> linked = new AtomicReference<>();
        PatientRepository patients = new PatientRepository() {
            public Optional<Patient> findByDocument(String type, String number) {
                lookup.set(type + ":" + number);
                return "CC:AB-123".equals(lookup.get()) ? Optional.of(existing) : Optional.empty();
            }
            public Optional<Patient> findById(PatientId id) { return Optional.empty(); }
            public Optional<Patient> findByUserId(UserId id) { return Optional.empty(); }
            public Patient save(Patient patient) { return patient; }
            public List<Patient> search(String query) { return List.of(); }
        };
        UserRepository users = new UserRepository() {
            public Optional<User> findByEmail(String email) { return Optional.empty(); }
            public Optional<User> findById(UserId id) { return Optional.empty(); }
            public User save(User user) { return user; }
            public User saveAndRevokeSessions(User user, Instant revokedAt) { return user; }
            public User registerPatientAccount(User user, Patient patient) { linked.set(patient); return user; }
            public void savePasswordReset(co.ufps.clinica.domain.identity.PasswordReset reset) {}
            public Optional<co.ufps.clinica.domain.identity.PasswordReset> findPasswordReset(String hash) { return Optional.empty(); }
            public void consumePasswordReset(co.ufps.clinica.domain.identity.PasswordReset reset, User user) {}
            public void saveAudit(co.ufps.clinica.domain.identity.AccessAudit audit) {}
            public co.ufps.clinica.domain.identity.UserSession saveSession(co.ufps.clinica.domain.identity.UserSession session) { return session; }
            public Optional<co.ufps.clinica.domain.identity.UserSession> findSessionByTokenHash(String hash) { return Optional.empty(); }
        };

        AccountService service = new AccountService(users, patients, new TestPasswords());
        User registered = service.registerPatient(" ANA@example.test ", "long-password-123", " cc ", " ab-123 ");

        assertEquals("CC:AB-123", lookup.get());
        assertEquals("ana@example.test", linked.get().email());
        assertEquals("CC", linked.get().documentType());
        assertEquals("AB-123", linked.get().documentNumber());
        assertEquals(registered.id(), linked.get().accountUserId());
    }

    @Test void administratorCanDeactivateAndChangeStaffRoleButCannotDisableSelf() {
        User administrator = user(Role.ADMINISTRADOR);
        User receptionist = user(Role.RECEPCION);
        Map<UserId, User> users = new HashMap<>();
        users.put(administrator.id(), administrator);
        users.put(receptionist.id(), receptionist);
        AccountService service = new AccountService(userRepository(users), patientRepository(), new TestPasswords());

        User deactivated = service.setStaffActive(Role.ADMINISTRADOR, administrator.id(), receptionist.id(), false);
        User pharmacist = service.changeStaffRole(Role.ADMINISTRADOR, administrator.id(), receptionist.id(), Role.FARMACIA);

        assertFalse(deactivated.active());
        assertEquals(Role.FARMACIA, pharmacist.role());
        assertThrows(IllegalArgumentException.class,
                () -> service.setStaffActive(Role.ADMINISTRADOR, administrator.id(), administrator.id(), false));
        User anotherAdministrator = user(Role.ADMINISTRADOR);
        users.put(anotherAdministrator.id(), anotherAdministrator);
        assertThrows(IllegalArgumentException.class,
                () -> service.setStaffActive(Role.ADMINISTRADOR, administrator.id(), anotherAdministrator.id(), false));
    }

    private User user(Role role) {
        return new User(UserId.generate(), "staff@example.test", "hash", role, true, Instant.now(), null);
    }

    private UserRepository userRepository(Map<UserId, User> data) {
        return new UserRepository() {
            public Optional<User> findByEmail(String email) { return data.values().stream().filter(u -> u.email().equals(email)).findFirst(); }
            public Optional<User> findById(UserId id) { return Optional.ofNullable(data.get(id)); }
            public User save(User user) { data.put(user.id(), user); return user; }
            public User saveAndRevokeSessions(User user, Instant revokedAt) { data.put(user.id(), user); return user; }
            public User registerPatientAccount(User user, Patient patient) { data.put(user.id(), user); return user; }
            public void savePasswordReset(co.ufps.clinica.domain.identity.PasswordReset reset) {}
            public Optional<co.ufps.clinica.domain.identity.PasswordReset> findPasswordReset(String hash) { return Optional.empty(); }
            public void consumePasswordReset(co.ufps.clinica.domain.identity.PasswordReset reset, User user) { data.put(user.id(), user); }
            public void saveAudit(co.ufps.clinica.domain.identity.AccessAudit audit) {}
            public co.ufps.clinica.domain.identity.UserSession saveSession(co.ufps.clinica.domain.identity.UserSession session) { return session; }
            public Optional<co.ufps.clinica.domain.identity.UserSession> findSessionByTokenHash(String hash) { return Optional.empty(); }
        };
    }

    private PatientRepository patientRepository() {
        return new PatientRepository() {
            public Optional<Patient> findByDocument(String type, String number) { return Optional.empty(); }
            public Optional<Patient> findById(PatientId id) { return Optional.empty(); }
            public Optional<Patient> findByUserId(UserId userId) { return Optional.empty(); }
            public Patient save(Patient patient) { return patient; }
            public List<Patient> search(String query) { return List.of(); }
        };
    }

    private static class TestPasswords implements AccountService.Passwords {
        public String hash(String plainText) { return "hash"; }
        public boolean matches(String plainText, String encodedHash) { return false; }
    }
}
