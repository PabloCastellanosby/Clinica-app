package co.ufps.clinica.application.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.ufps.clinica.application.port.out.communication.CommunicationRepository;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.domain.communication.MessageStatus;
import co.ufps.clinica.domain.communication.TransactionalMessage;
import co.ufps.clinica.domain.communication.TransactionalMessageId;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CommunicationServiceTest {
    @Test void retriesFailedDeliveryFiveTimesThenMarksItFailed() {
        var repository = new InMemoryCommunicationRepository();
        var service = service(repository, (recipient, subject, body) -> { throw new IllegalStateException("SMTP unavailable"); });
        var pending = new TransactionalMessage(TransactionalMessageId.generate(), null, "patient@example.test",
                "Appointment", "Details", MessageStatus.PENDING, Instant.now(), null);
        repository.saveMessage(pending);

        for (int attempt = 0; attempt < 5; attempt++) {
            assertEquals(1, service.dispatchPending(10));
        }

        var failed = repository.findById(pending.id()).orElseThrow();
        assertEquals(MessageStatus.FAILED, failed.status());
        assertEquals(5, failed.attempts());
        assertEquals(0, service.dispatchPending(10));
    }

    @Test void successfulDeliveryMarksMessageSentAndDoesNotRetry() {
        var repository = new InMemoryCommunicationRepository();
        var service = service(repository, (recipient, subject, body) -> { });
        var pending = new TransactionalMessage(TransactionalMessageId.generate(), null, "patient@example.test",
                "Appointment", "Details", MessageStatus.PENDING, Instant.now(), null);
        repository.saveMessage(pending);

        assertEquals(1, service.dispatchPending(10));
        var sent = repository.findById(pending.id()).orElseThrow();
        assertEquals(MessageStatus.SENT, sent.status());
        assertTrue(sent.sentAt() != null);
        assertEquals(1, sent.attempts());
        assertEquals(0, service.dispatchPending(10));
    }

    private CommunicationService service(CommunicationRepository repository,
            co.ufps.clinica.application.port.out.communication.EmailGateway emailGateway) {
        PatientRepository patients = proxy(PatientRepository.class);
        return new CommunicationService(repository, emailGateway, patients);
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
                (proxy, method, args) -> Optional.empty());
    }

    private static final class InMemoryCommunicationRepository implements CommunicationRepository {
        private final List<TransactionalMessage> messages = new ArrayList<>();
        @Override public TransactionalMessage saveMessage(TransactionalMessage message) {
            messages.removeIf(existing -> existing.id().equals(message.id()));
            messages.add(message);
            return message;
        }
        @Override public Optional<TransactionalMessage> findById(TransactionalMessageId id) {
            return messages.stream().filter(message -> message.id().equals(id)).findFirst();
        }
        @Override public List<TransactionalMessage> findByRecipient(co.ufps.clinica.domain.identity.UserId id) { return List.of(); }
        @Override public List<TransactionalMessage> claimPendingMessages(int limit) {
            List<TransactionalMessage> candidates = messages.stream()
                    .filter(message -> message.status() == MessageStatus.PENDING && message.attempts() < 5)
                    .limit(limit).toList();
            List<TransactionalMessage> claimedMessages = new ArrayList<>();
            for (TransactionalMessage message : candidates) {
                var claimed = new TransactionalMessage(message.id(), message.recipientId(), message.destination(),
                        message.subject(), message.template(), MessageStatus.SENDING, message.createdAt(), null,
                        message.attempts() + 1, Instant.now());
                saveMessage(claimed);
                claimedMessages.add(claimed);
            }
            return claimedMessages;
        }
        @Override public co.ufps.clinica.domain.communication.PatientNotice saveNotice(
                co.ufps.clinica.domain.communication.PatientNotice notice) { return notice; }
        @Override public Optional<co.ufps.clinica.domain.communication.PatientNotice> findByIdNotice(
                co.ufps.clinica.domain.communication.PatientNoticeId id) { return Optional.empty(); }
        @Override public List<co.ufps.clinica.domain.communication.PatientNotice> findActiveNotices(
                co.ufps.clinica.domain.clinic.BranchId id) { return List.of(); }
    }
}
