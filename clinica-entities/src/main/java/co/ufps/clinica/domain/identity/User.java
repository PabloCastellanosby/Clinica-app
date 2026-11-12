package co.ufps.clinica.domain.identity;
import java.time.Instant;
public record User(UserId id, String email, String passwordHash, Role role, boolean active, Instant createdAt, Instant lastLoginAt) {
    public User {
        if (id == null || email == null || email.isBlank() || passwordHash == null || passwordHash.isBlank()
                || role == null || createdAt == null)
            throw new IllegalArgumentException("La cuenta debe tener identificador, correo, hash, rol y fecha de creación");
    }
}
