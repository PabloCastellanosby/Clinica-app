package co.ufps.clinica.application.appointment;

import co.ufps.clinica.application.communication.NotificationGateway;
import co.ufps.clinica.application.port.out.appointment.AppointmentRepository;
import co.ufps.clinica.domain.appointment.AppointmentReminder;
import java.time.Instant;
import java.util.Objects;

public final class ReminderService {
    private static final int MAX_ATTEMPTS = 5;
    private final AppointmentRepository appointments;
    private final NotificationGateway notifications;
    public ReminderService(AppointmentRepository appointments, NotificationGateway notifications) {
        this.appointments = Objects.requireNonNull(appointments);
        this.notifications = Objects.requireNonNull(notifications);
    }
    public int dispatchDue(Instant now) {
        int processed = 0;
        for (AppointmentReminder reminder : appointments.findDueReminders(now, 100)) {
            var appointment = appointments.findById(reminder.appointmentId()).orElse(null);
            boolean sent = appointment != null && notifications.sendReminder(appointment);
            AppointmentReminder attempted = reminder.attempted(sent, now);
            if (attempted.attempts() >= MAX_ATTEMPTS && !sent) attempted = new AppointmentReminder(
                    attempted.id(), attempted.appointmentId(), attempted.scheduledAt(), attempted.sentAt(),
                    attempted.channel(), MAX_ATTEMPTS, attempted.lastAttemptAt());
            appointments.saveReminder(attempted);
            processed++;
        }
        return processed;
    }
}
