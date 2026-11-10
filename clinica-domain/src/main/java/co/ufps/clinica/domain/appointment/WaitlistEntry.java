package co.ufps.clinica.domain.appointment;
import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;
public record WaitlistEntry(WaitlistEntryId id, PatientId patientId, DoctorId doctorId, SpecialtyId specialtyId,
                            Instant requestedFrom, Instant requestedUntil, Instant createdAt) {}
