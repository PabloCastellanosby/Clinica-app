package co.ufps.clinica.domain.communication;
import co.ufps.clinica.domain.identity.UserId;
import java.time.Instant;
public record TransactionalMessage(TransactionalMessageId id, UserId recipientId, String destination, String subject,
                                   String template, MessageStatus status, Instant createdAt, Instant sentAt, int attempts,
                                   Instant lastAttemptAt) {
    public TransactionalMessage(TransactionalMessageId id, UserId recipientId, String destination, String subject,
                                String template, MessageStatus status, Instant createdAt, Instant sentAt) {
        this(id, recipientId, destination, subject, template, status, createdAt, sentAt, 0);
    }
    public TransactionalMessage(TransactionalMessageId id, UserId recipientId, String destination, String subject,
                                String template, MessageStatus status, Instant createdAt, Instant sentAt, int attempts) {
        this(id, recipientId, destination, subject, template, status, createdAt, sentAt, attempts, null);
    }
}
