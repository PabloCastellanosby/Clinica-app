package co.ufps.clinica.application.communication;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.communication.PatientNotice;
public interface NotificationGateway {
    void sendAppointmentConfirmation(Appointment appointment);
    void sendReminder(Appointment appointment);
    void publish(PatientNotice notice);
}
