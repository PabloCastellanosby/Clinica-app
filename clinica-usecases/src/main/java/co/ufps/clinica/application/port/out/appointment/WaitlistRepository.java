package co.ufps.clinica.application.port.out.appointment;

import co.ufps.clinica.domain.appointment.WaitlistEntry;
import co.ufps.clinica.domain.appointment.WaitlistEntryId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WaitlistRepository {
    WaitlistEntry save(WaitlistEntry entry);
    Optional<WaitlistEntry> findById(WaitlistEntryId id);
    List<WaitlistEntry> findByPatient(PatientId patientId);
    Optional<WaitlistEntry> offerNextFor(DoctorId doctorId, Instant slotStart);
    void expireBefore(Instant now);
}
