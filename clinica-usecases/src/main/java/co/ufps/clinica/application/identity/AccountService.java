package co.ufps.clinica.application.identity;

import co.ufps.clinica.domain.identity.*;
import co.ufps.clinica.application.port.out.identity.UserRepository;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import java.time.Instant;
import java.util.*;

/** Registro, acceso, expiración y recuperación. El adaptador almacena únicamente hashes. */
public final class AccountService {
    public interface Passwords { String hash(String plainText); boolean matches(String plainText, String encodedHash); }
    private final UserRepository repository;
    private final PatientRepository patients;
    private final Passwords passwords;
    public AccountService(UserRepository repository, PatientRepository patients, Passwords passwords) {
        this.repository = Objects.requireNonNull(repository);
        this.patients = Objects.requireNonNull(patients);
        this.passwords = Objects.requireNonNull(passwords);
    }
    public User registerPatient(String email, String password, String documentType, String documentNumber) {
        if (documentType == null || documentType.isBlank() || documentNumber == null || documentNumber.isBlank())
            throw new IllegalArgumentException("Tipo y número de documento son obligatorios para vincular la cuenta");
        String normalizedDocumentType = documentType.trim().toUpperCase(Locale.ROOT);
        String normalizedDocumentNumber = documentNumber.trim().toUpperCase(Locale.ROOT);
        var patient = patients.findByDocument(normalizedDocumentType, normalizedDocumentNumber)
                .orElseThrow(() -> new IllegalArgumentException("No existe un perfil con esos datos de documento"));
        if (patient.accountUserId() != null) throw new IllegalArgumentException("El paciente ya tiene una cuenta asociada");
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (patient.email() == null || patient.email().isBlank() || !patient.email().trim().equalsIgnoreCase(normalizedEmail))
            throw new IllegalArgumentException("El correo no coincide con el registrado en el perfil");
        User user = prepareUser(normalizedEmail, password, Role.PACIENTE);
        return repository.registerPatientAccount(user, patient.linkedToAccount(user.id()));
    }
    public User bootstrapAdministrator(String email, String password) {
        String normalized = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        Optional<User> current = repository.findByEmail(normalized);
        if (current.isPresent()) {
            if (current.get().role() != Role.ADMINISTRADOR) throw new IllegalStateException("La cuenta configurada para bootstrap no es administradora");
            return current.get();
        }
        return create(normalized, password, Role.ADMINISTRADOR);
    }
    public User provisionStaff(Role actorRole, String email, String password, Role role) {
        if (actorRole != Role.ADMINISTRADOR || role == Role.PACIENTE || role == Role.ADMINISTRADOR) throw new SecurityException("Solo un administrador puede crear cuentas del personal");
        return create(email, password, role);
    }
    public User setStaffActive(Role actorRole, UserId actorId, UserId targetId, boolean active) {
        if (actorRole != Role.ADMINISTRADOR) throw new SecurityException("Solo un administrador puede gestionar el personal");
        if (!active && actorId.equals(targetId)) throw new IllegalArgumentException("No puede desactivar la cuenta administradora que está utilizando");
        User current = repository.findById(targetId).orElseThrow(() -> new NoSuchElementException("Cuenta no encontrada"));
        if (current.role() == Role.PACIENTE || current.role() == Role.ADMINISTRADOR)
            throw new IllegalArgumentException("Esta operación solo aplica a cuentas de personal no administrativas");
        User updated = new User(current.id(), current.email(), current.passwordHash(), current.role(), active,
                current.createdAt(), current.lastLoginAt());
        return active ? repository.save(updated) : repository.saveAndRevokeSessions(updated, Instant.now());
    }
    public User changeStaffRole(Role actorRole, UserId actorId, UserId targetId, Role newRole) {
        if (actorRole != Role.ADMINISTRADOR) throw new SecurityException("Solo un administrador puede gestionar los roles del personal");
        if (actorId.equals(targetId)) throw new IllegalArgumentException("No puede cambiar el rol de la cuenta administradora que está utilizando");
        if (newRole == null || newRole == Role.ADMINISTRADOR || newRole == Role.PACIENTE)
            throw new IllegalArgumentException("El rol indicado no se puede asignar desde la gestión de personal");
        User current = repository.findById(targetId).orElseThrow(() -> new NoSuchElementException("Cuenta no encontrada"));
        if (current.role() == Role.PACIENTE || current.role() == Role.ADMINISTRADOR)
            throw new IllegalArgumentException("Esta operación solo aplica a cuentas de personal no administrativas");
        return repository.save(new User(current.id(), current.email(), current.passwordHash(), newRole,
                current.active(), current.createdAt(), current.lastLoginAt()));
    }
    private User create(String email, String password, Role role) {
        return repository.save(prepareUser(email, password, role));
    }
    private User prepareUser(String email, String password, Role role) {
        if (email == null || email.isBlank() || email.length() > 254 || password == null
                || password.length() < 10 || password.length() > 128)
            throw new IllegalArgumentException("Email obligatorio y contraseña de entre 10 y 128 caracteres");
        if (repository.findByEmail(email.trim().toLowerCase(Locale.ROOT)).isPresent()) throw new IllegalArgumentException("El correo ya está registrado");
        return new User(UserId.generate(), email.trim().toLowerCase(Locale.ROOT), passwords.hash(password), role, true, Instant.now(), null);
    }
    public User authenticate(String email, String password, String ipAddress) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        User found = repository.findByEmail(normalizedEmail).orElse(null);
        boolean accepted = found != null && found.active() && passwords.matches(password, found.passwordHash());
        repository.saveAudit(new AccessAudit(AccessAuditId.generate(), found == null ? null : found.id(), "LOGIN", ipAddress, Instant.now(), accepted));
        if (!accepted) throw new InvalidCredentialsException();
        return repository.save(new User(found.id(), found.email(), found.passwordHash(), found.role(), true, found.createdAt(), Instant.now()));
    }
}
