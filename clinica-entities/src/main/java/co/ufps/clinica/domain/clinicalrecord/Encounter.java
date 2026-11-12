package co.ufps.clinica.domain.clinicalrecord;
import co.ufps.clinica.domain.appointment.AppointmentId;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;
public record Encounter(EncounterId id, AppointmentId appointmentId, PatientId patientId, DoctorId doctorId,
                        Instant occurredAt, String reason, String diagnosis, String notes, boolean patientVisible) {
    public Encounter {
        if (id == null || appointmentId == null || patientId == null || doctorId == null || occurredAt == null)
            throw new IllegalArgumentException("El encuentro debe incluir cita, paciente, médico y fecha");
    }
}
