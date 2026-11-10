package co.ufps.clinica.application.agenda;
import co.ufps.clinica.domain.appointment.Appointment;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;
import java.util.List;
public interface DoctorAgendaQuery {
    List<Appointment> forDoctor(DoctorId doctorId, Instant from, Instant until);
}
