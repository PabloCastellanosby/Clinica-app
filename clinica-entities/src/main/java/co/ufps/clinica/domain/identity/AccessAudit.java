package co.ufps.clinica.domain.identity;
import java.time.Instant;
public record AccessAudit(AccessAuditId id, UserId userId, String action, String ipAddress, Instant occurredAt, boolean successful) {}
