package co.ufps.clinica.domain.communication;
import co.ufps.clinica.domain.identity.UserId;
import java.time.Instant;
public record TransactionalMessage(TransactionalMessageId id, UserId recipientId, String destination, String subject,
                                   String template, MessageStatus status, Instant createdAt, Instant sentAt) {}
