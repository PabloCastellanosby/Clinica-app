package co.ufps.clinica.domain.appointment;
import co.ufps.clinica.domain.communication.NotificationChannel;
import java.time.Instant;
public record AppointmentReminder(AppointmentReminderId id, AppointmentId appointmentId, Instant scheduledAt,
                                  Instant sentAt, NotificationChannel channel, int attempts, Instant lastAttemptAt) {
    public AppointmentReminder {
        if (id == null || appointmentId == null || scheduledAt == null || channel == null || attempts < 0)
            throw new IllegalArgumentException("El recordatorio de cita está incompleto");
    }
    public AppointmentReminder claimedAt(Instant now) {
        if (now == null || sentAt != null || attempts >= 5)
            throw new IllegalStateException("El recordatorio ya fue enviado o agotó sus intentos");
        return new AppointmentReminder(id, appointmentId, scheduledAt, null, channel, attempts + 1, now);
    }

    public AppointmentReminder deliveryResult(boolean sent, Instant now) {
        if (now == null || lastAttemptAt == null)
            throw new IllegalStateException("El recordatorio debe reclamarse antes de guardar el resultado");
        return new AppointmentReminder(id, appointmentId, scheduledAt, sent ? now : sentAt,
                channel, attempts, lastAttemptAt);
    }
}
