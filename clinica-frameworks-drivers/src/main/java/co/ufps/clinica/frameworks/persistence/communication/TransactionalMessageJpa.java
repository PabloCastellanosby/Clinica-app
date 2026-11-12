package co.ufps.clinica.frameworks.persistence.communication;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "TRANSACTIONAL_MESSAGES", indexes = @Index(name = "idx_message_recipient", columnList = "recipient_id"))
public class TransactionalMessageJpa {
    @Id @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    @Column(name = "recipient_id")
    private UUID recipientId;
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "recipient_id", insertable = false, updatable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_transactional_messages_recipient"))
    private co.ufps.clinica.frameworks.persistence.identity.UserJpa recipientReference;
    @Column(name = "destination", length = 254)
    private String destination;
    @Column(name = "subject", length = 500)
    private String subject;
    @Column(name = "template", length = 200)
    private String template;
    @Column(name = "status", length = 20, nullable = false)
    private String status;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "sent_at")
    private Instant sentAt;
    @Column(name = "attempts", nullable = false)
    private int attempts;
    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    public TransactionalMessageJpa() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRecipientId() { return recipientId; }
    public void setRecipientId(UUID recipientId) { this.recipientId = recipientId; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public Instant getLastAttemptAt() { return lastAttemptAt; }
    public void setLastAttemptAt(Instant lastAttemptAt) { this.lastAttemptAt = lastAttemptAt; }
}
