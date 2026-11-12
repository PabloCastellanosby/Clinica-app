package co.ufps.clinica.application.communication;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.communication.PatientNotice;
import co.ufps.clinica.domain.appointment.WaitlistEntry;
public interface NotificationGateway {
    void sendAppointmentRequestReceived(Appointment appointment);
    void sendAppointmentConfirmed(Appointment appointment);
    /** Returns true once the notification is durably accepted for delivery, not only after SMTP success. */
    boolean sendReminder(Appointment appointment);
    void sendWaitlistOffer(WaitlistEntry entry, Appointment cancelledAppointment);
    void publish(PatientNotice notice);
}
