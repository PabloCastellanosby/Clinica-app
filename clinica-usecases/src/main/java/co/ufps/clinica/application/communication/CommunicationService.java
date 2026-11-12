package co.ufps.clinica.application.communication;

import co.ufps.clinica.domain.communication.*;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.appointment.WaitlistEntry;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.clinic.BranchId;
import co.ufps.clinica.application.port.out.communication.CommunicationRepository;
import co.ufps.clinica.application.port.out.communication.EmailGateway;
import co.ufps.clinica.application.port.out.patient.PatientRepository;
import java.time.Instant;
import java.util.*;

public final class CommunicationService implements NotificationGateway {
    private final CommunicationRepository repository;
    private final EmailGateway emailGateway;
    private final PatientRepository patients;
    public CommunicationService(CommunicationRepository repository, EmailGateway emailGateway, PatientRepository patients) {
        this.repository = Objects.requireNonNull(repository);
        this.emailGateway = Objects.requireNonNull(emailGateway);
        this.patients = Objects.requireNonNull(patients);
    }

    public TransactionalMessage sendMessage(SendMessageCommand command) {
        TransactionalMessage message = new TransactionalMessage(TransactionalMessageId.generate(),
                command.recipientId(), command.destination(), command.subject(),
                command.template(), MessageStatus.PENDING, Instant.now(), null);
        repository.saveMessage(message);
        dispatchPending(1);
        return repository.findById(message.id()).orElse(message);
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
        var patient = patients.findById(appointment.patientId()).orElse(null);
        TransactionalMessage message = new TransactionalMessage(TransactionalMessageId.generate(),
                patient == null ? null : patient.accountUserId(), patient == null ? "" : patient.email(), "Confirmación de cita",
                "Su cita clínica está confirmada para " + appointment.startsAt() + ".", MessageStatus.PENDING, Instant.now(), null);
        repository.saveMessage(message);
        dispatchPending(1);
    }

    @Override
    public boolean sendReminder(Appointment appointment) {
        var patient = patients.findById(appointment.patientId()).orElse(null);
        TransactionalMessage message = new TransactionalMessage(TransactionalMessageId.generate(),
                patient == null ? null : patient.accountUserId(), patient == null ? "" : patient.email(), "Recordatorio de cita",
                "Le recordamos su cita clínica programada para " + appointment.startsAt() + ".", MessageStatus.PENDING, Instant.now(), null);
        repository.saveMessage(message);
        dispatchPending(1);
        return repository.findById(message.id()).map(m -> m.status() == MessageStatus.SENT).orElse(false);
    }

    @Override
    public void publish(PatientNotice notice) {
        repository.saveNotice(notice);
    }

    @Override public void sendWaitlistOffer(WaitlistEntry entry, Appointment cancelledAppointment) {
        var patient = patients.findById(entry.patientId()).orElse(null);
        TransactionalMessage message = new TransactionalMessage(TransactionalMessageId.generate(),
                patient == null ? null : patient.accountUserId(), patient == null ? "" : patient.email(),
                "Cupo disponible para su cita", "Se liberó un horario que coincide con su solicitud: "
                + cancelledAppointment.startsAt() + ". Ingrese a su cuenta para reservarlo.",
                MessageStatus.PENDING, Instant.now(), null);
        repository.saveMessage(message);
        dispatchPending(1);
    }

    /** Entrega mensajes pendientes con límite de reintentos. El reclamo transaccional evita
     * que dos instancias despachen el mismo mensaje simultáneamente. */
    public int dispatchPending(int limit) {
        List<TransactionalMessage> claimed = repository.claimPendingMessages(limit);
        for (TransactionalMessage message : claimed) {
            MessageStatus status;
            Instant sentAt = null;
            try {
                if (message.destination() == null || !message.destination().contains("@"))
                    throw new IllegalArgumentException("El destinatario no tiene un correo válido");
                emailGateway.send(message.destination(), message.subject(), message.template());
                status = MessageStatus.SENT;
                sentAt = Instant.now();
            } catch (RuntimeException ex) {
                status = message.attempts() >= 5 ? MessageStatus.FAILED : MessageStatus.PENDING;
            }
            repository.saveMessage(new TransactionalMessage(message.id(), message.recipientId(), message.destination(),
                    message.subject(), message.template(), status, message.createdAt(), sentAt, message.attempts(), message.lastAttemptAt()));
        }
        return claimed.size();
    }

}
