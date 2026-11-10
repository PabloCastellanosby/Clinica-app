package co.ufps.clinica.frameworks.persistence.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "access_audit", indexes = @Index(name = "idx_access_audit_user_time", columnList = "user_id, occurred_at"))
public class AccessAuditJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "action", length = 80, nullable = false)
    private String action;
    @Column(name = "ip_address", length = 80)
    private String ipAddress;
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;
    @Column(name = "successful", nullable = false)
    private boolean successful;

    public AccessAuditJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
}
