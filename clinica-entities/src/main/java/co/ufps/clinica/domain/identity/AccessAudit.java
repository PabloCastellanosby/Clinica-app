package co.ufps.clinica.domain.identity;
import java.time.Instant;
public record AccessAudit(AccessAuditId id, UserId userId, String action, String ipAddress, Instant occurredAt, boolean successful) {
    public AccessAudit {
        if (id == null || action == null || action.isBlank() || action.length() > 80 || occurredAt == null)
            throw new IllegalArgumentException("El registro de auditoría debe tener acción y fecha válidas");
        if (ipAddress != null && ipAddress.length() > 80)
            throw new IllegalArgumentException("La dirección IP supera la longitud permitida");
    }
}
