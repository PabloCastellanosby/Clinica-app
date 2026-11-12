package co.ufps.clinica.domain.identity;

import java.time.Instant;

public record UserSession(UserSessionId id, UserId userId, String tokenHash,
                          Instant createdAt, Instant expiresAt, Instant revokedAt) {
    public boolean isActiveAt(Instant now) {
        return revokedAt == null && expiresAt != null && expiresAt.isAfter(now);
    }
    public UserSession revoke(Instant at) {
        return new UserSession(id, userId, tokenHash, createdAt, expiresAt, at);
    }
}
