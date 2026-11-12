package co.ufps.clinica.application.appointment;

import co.ufps.clinica.application.communication.NotificationGateway;
import co.ufps.clinica.application.port.out.appointment.AppointmentRepository;
import co.ufps.clinica.domain.appointment.AppointmentReminder;
import java.time.Instant;
import java.util.Objects;

public final class ReminderService {
    private final AppointmentRepository appointments;
    private final NotificationGateway notifications;
    public ReminderService(AppointmentRepository appointments, NotificationGateway notifications) {
        this.appointments = Objects.requireNonNull(appointments);
        this.notifications = Objects.requireNonNull(notifications);
    }
    public int dispatchDue(Instant now) {
        int processed = 0;
        for (AppointmentReminder reminder : appointments.claimDueReminders(now, 100)) {
            var appointment = appointments.findById(reminder.appointmentId()).orElse(null);
            boolean sent = appointment != null && notifications.sendReminder(appointment);
            appointments.saveReminder(reminder.deliveryResult(sent, now));
            processed++;
        }
        return processed;
    }
}
