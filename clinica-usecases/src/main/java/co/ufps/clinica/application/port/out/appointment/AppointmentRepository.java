package co.ufps.clinica.application.port.out.appointment;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.appointment.AppointmentReminder;
import co.ufps.clinica.domain.clinic.OfficeId;
import co.ufps.clinica.domain.staff.DoctorId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.appointment.WaitlistEntryId;
import java.time.Instant;
import java.util.Optional;
public interface AppointmentRepository {
    boolean hasConflict(PatientId patientId, DoctorId doctorId, OfficeId officeId, Instant from, Instant until);
    Appointment save(Appointment appointment);
    Appointment createIfAvailable(Appointment appointment, AppointmentReminder reminder);
    Appointment createFromWaitlistIfAvailable(Appointment appointment, AppointmentReminder reminder,
                                              WaitlistEntryId offerId, PatientId patientId);
    Optional<Appointment> findById(AppointmentId id);
    java.util.List<Appointment> findForDoctor(DoctorId doctorId, Instant from, Instant until);
    java.util.List<Appointment> findForPatient(PatientId patientId, Instant from, Instant until);
    boolean hasClinicalRelationship(DoctorId doctorId, PatientId patientId);
    AppointmentReminder saveReminder(AppointmentReminder reminder);
    java.util.List<AppointmentReminder> claimDueReminders(Instant now, int limit);
}
