package co.ufps.clinica.domain.clinicalrecord;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;
public record Encounter(EncounterId id, AppointmentId appointmentId, PatientId patientId, DoctorId doctorId,
                        Instant occurredAt, String reason, String diagnosis, String notes, boolean patientVisible) {}
