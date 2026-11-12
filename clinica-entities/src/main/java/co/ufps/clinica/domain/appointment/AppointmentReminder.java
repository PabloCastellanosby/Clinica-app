package co.ufps.clinica.domain.appointment;
import co.ufps.clinica.domain.communication.NotificationChannel;
import java.time.Instant;
public record AppointmentReminder(AppointmentReminderId id, AppointmentId appointmentId, Instant scheduledAt,
                                  Instant sentAt, NotificationChannel channel, int attempts, Instant lastAttemptAt) {
    public AppointmentReminder {
        if (id == null || appointmentId == null || scheduledAt == null || channel == null || attempts < 0)
            throw new IllegalArgumentException("El recordatorio de cita está incompleto");
    }
    public AppointmentReminder attempted(boolean sent, Instant now) {
        return new AppointmentReminder(id, appointmentId, scheduledAt, sent ? now : sentAt, channel, attempts + 1, now);
    }
}
