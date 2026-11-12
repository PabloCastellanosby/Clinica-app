package co.ufps.clinica.application.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.ufps.clinica.application.port.out.communication.CommunicationRepository;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.communication.MessageStatus;
import co.ufps.clinica.domain.communication.TransactionalMessage;
import co.ufps.clinica.domain.communication.TransactionalMessageId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.domain.clinic.OfficeId;
import co.ufps.clinica.domain.patient.Patient;
import co.ufps.clinica.domain.staff.DoctorId;
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

    @Test void reminderIsAcceptedByTheOutboxSoTheAppointmentJobDoesNotCreateDuplicateRetries() {
        var repository = new InMemoryCommunicationRepository();
        Patient patient = Patient.register("CC", "7788", "Laura", "Pérez", null,
                "patient@example.test", null, null, null);
        PatientRepository patients = proxy(PatientRepository.class, (method, args) ->
                method.equals("findById") ? Optional.of(patient) : Optional.empty());
        var service = new CommunicationService(repository,
                (recipient, subject, body) -> { throw new IllegalStateException("SMTP unavailable"); }, patients);
        Instant startsAt = Instant.now().plusSeconds(3600);
        Appointment appointment = Appointment.schedule(patient.id(), DoctorId.generate(), BranchId.generate(),
                OfficeId.generate(), null, startsAt, startsAt.plusSeconds(1800), Instant.now());

        assertTrue(service.sendReminder(appointment));
        assertEquals(1, repository.messages.size());
        assertEquals(MessageStatus.PENDING, repository.messages.get(0).status());
        assertEquals(1, repository.messages.get(0).attempts());
    }

    @Test void confirmedAppointmentQueuesAConfirmationMessageForItsPatient() {
        var repository = new InMemoryCommunicationRepository();
        Patient patient = Patient.register("CC", "9911", "Marta", "López", null,
                "marta@example.test", null, null, null);
        PatientRepository patients = proxy(PatientRepository.class, (method, args) ->
                method.equals("findById") ? Optional.of(patient) : Optional.empty());
        var service = new CommunicationService(repository, (recipient, subject, body) -> { }, patients);
        Instant startsAt = Instant.now().plusSeconds(7200);
        Appointment appointment = Appointment.schedule(patient.id(), DoctorId.generate(), BranchId.generate(),
                OfficeId.generate(), null, startsAt, startsAt.plusSeconds(1800), Instant.now());

        service.sendAppointmentConfirmed(appointment);

        assertEquals(1, repository.messages.size());
        assertEquals("marta@example.test", repository.messages.get(0).destination());
        assertEquals("Cita confirmada", repository.messages.get(0).subject());
        assertTrue(repository.messages.get(0).template().contains("La clínica confirmó su cita"));
        assertEquals(MessageStatus.SENT, repository.messages.get(0).status());
    }

    private CommunicationService service(CommunicationRepository repository,
            co.ufps.clinica.application.port.out.communication.EmailGateway emailGateway) {
        PatientRepository patients = proxy(PatientRepository.class);
        return new CommunicationService(repository, emailGateway, patients);
    }

    private interface Invocation { Object call(String method, Object[] args); }
    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type) {
        return proxy(type, (method, args) -> Optional.empty());
    }
    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type, Invocation invocation) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
                (proxy, method, args) -> invocation.call(method.getName(), args));
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
