package co.ufps.clinica.domain.identity;
import java.time.Instant;
public record User(UserId id, String email, String passwordHash, Role role, boolean active, Instant createdAt, Instant lastLoginAt) {}
