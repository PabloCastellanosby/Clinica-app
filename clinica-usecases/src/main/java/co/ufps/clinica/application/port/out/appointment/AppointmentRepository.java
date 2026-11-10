package co.ufps.clinica.application.port.out.appointment;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.clinic.OfficeId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;
import java.util.Optional;
public interface AppointmentRepository {
    boolean hasConflict(DoctorId doctorId, OfficeId officeId, Instant from, Instant until);
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(AppointmentId id);
}
