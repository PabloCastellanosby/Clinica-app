package co.ufps.clinica.domain.identity;
import java.time.Instant;
public record PasswordReset(PasswordResetId id, UserId userId, String tokenHash, Instant expiresAt, Instant usedAt) {
    public boolean isUsableAt(Instant now) { return usedAt == null && expiresAt != null && expiresAt.isAfter(now); }
    public PasswordReset markUsed(Instant now) { return new PasswordReset(id, userId, tokenHash, expiresAt, now); }
}
