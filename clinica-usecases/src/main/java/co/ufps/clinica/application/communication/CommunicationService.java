package co.ufps.clinica.application.communication;

import co.ufps.clinica.domain.communication.*;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.application.port.out.communication.CommunicationRepository;
import java.time.Instant;
import java.util.*;

public final class CommunicationService implements NotificationGateway {
    private final CommunicationRepository repository;
    public CommunicationService(CommunicationRepository repository) { this.repository = Objects.requireNonNull(repository); }

    public TransactionalMessage sendMessage(SendMessageCommand command) {
        TransactionalMessage message = new TransactionalMessage(TransactionalMessageId.generate(),
                command.recipientId(), command.destination(), command.subject(),
                command.template(), MessageStatus.PENDING, Instant.now(), null);
        return repository.saveMessage(message);
    }

    public PatientNotice publishNotice(PublishNoticeCommand command) {
        PatientNotice notice = new PatientNotice(PatientNoticeId.generate(),
                command.title(), command.body(), Instant.now(),
                command.expiresAt(), command.branchId());
        return repository.saveNotice(notice);
    }

    public List<PatientNotice> getActiveNotices(BranchId branchId) {
        return repository.findActiveNotices(branchId);
    }

    public List<TransactionalMessage> getMessagesForUser(UserId userId) {
        return repository.findByRecipient(userId);
    }

    @Override
    public void sendAppointmentConfirmation(Appointment appointment) {
        TransactionalMessage message = new TransactionalMessage(TransactionalMessageId.generate(),
                null, "appointment-confirmation", "Confirmación de cita",
                "appointment_confirmation", MessageStatus.PENDING, Instant.now(), null);
        repository.saveMessage(message);
    }

    @Override
    public void sendReminder(Appointment appointment) {
        TransactionalMessage message = new TransactionalMessage(TransactionalMessageId.generate(),
                null, "appointment-reminder", "Recordatorio de cita",
                "appointment_reminder", MessageStatus.PENDING, Instant.now(), null);
        repository.saveMessage(message);
    }

    @Override
    public void publish(PatientNotice notice) {
        repository.saveNotice(notice);
    }
}
