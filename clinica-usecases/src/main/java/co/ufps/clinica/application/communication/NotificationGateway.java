package co.ufps.clinica.application.communication;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.communication.PatientNotice;
import co.ufps.clinica.domain.appointment.WaitlistEntry;
public interface NotificationGateway {
    void sendAppointmentConfirmation(Appointment appointment);
    boolean sendReminder(Appointment appointment);
    void sendWaitlistOffer(WaitlistEntry entry, Appointment cancelledAppointment);
    void publish(PatientNotice notice);
}
