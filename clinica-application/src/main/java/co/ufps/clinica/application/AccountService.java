package co.ufps.clinica.application;

import co.ufps.clinica.domain.ClinicModel.*;
import co.ufps.clinica.domain.Role;
import co.ufps.clinica.domain.UserRepository;
import co.ufps.clinica.domain.UserId;
import co.ufps.clinica.domain.AccessAuditId;
import java.time.Instant;
import java.util.*;

/** Registro, acceso, expiración y recuperación. El adaptador almacena únicamente hashes. */
public final class AccountService {
    public interface Passwords { String hash(String plainText); boolean matches(String plainText, String encodedHash); }
    private final UserRepository repository;
    private final Passwords passwords;
    public AccountService(UserRepository repository, Passwords passwords) { this.repository = Objects.requireNonNull(repository); this.passwords = Objects.requireNonNull(passwords); }
    public User registerPatient(String email, String password) {
        return create(email, password, Role.PACIENTE);
    }
    public User provisionStaff(Role actorRole, String email, String password, Role role) {
        if (actorRole != Role.ADMINISTRADOR || role == Role.PACIENTE || role == Role.ADMINISTRADOR) throw new SecurityException("Solo un administrador puede crear cuentas del personal");
        return create(email, password, role);
    }
    private User create(String email, String password, Role role) {
        if (email == null || email.isBlank() || password == null || password.length() < 10) throw new IllegalArgumentException("Email obligatorio y contraseña de al menos 10 caracteres");
        if (repository.findByEmail(email.trim().toLowerCase(Locale.ROOT)).isPresent()) throw new IllegalArgumentException("El correo ya está registrado");
        return repository.save(new User(UserId.generate(), email.trim().toLowerCase(Locale.ROOT), passwords.hash(password), role, true, Instant.now(), null));
    }
    public User authenticate(String email, String password, String ipAddress) {
        User found = repository.findByEmail(email.trim().toLowerCase(Locale.ROOT)).orElse(null);
        boolean accepted = found != null && found.active() && passwords.matches(password, found.passwordHash());
        repository.saveAudit(new AccessAudit(AccessAuditId.generate(), found == null ? null : found.id(), "LOGIN", ipAddress, Instant.now(), accepted));
        if (!accepted) throw new SecurityException("Credenciales inválidas");
        return repository.save(new User(found.id(), found.email(), found.passwordHash(), found.role(), true, found.createdAt(), Instant.now()));
    }
}
