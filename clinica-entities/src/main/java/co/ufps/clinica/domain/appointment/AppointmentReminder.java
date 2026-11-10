package co.ufps.clinica.domain.appointment;
import co.ufps.clinica.domain.communication.NotificationChannel;
import java.time.Instant;
public record AppointmentReminder(AppointmentReminderId id, AppointmentId appointmentId, Instant scheduledAt,
                                  Instant sentAt, NotificationChannel channel) {}
