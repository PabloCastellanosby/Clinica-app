package co.ufps.clinica.domain.identity;
import java.time.Instant;
public record PasswordReset(PasswordResetId id, UserId userId, String tokenHash, Instant expiresAt, Instant usedAt) {}
