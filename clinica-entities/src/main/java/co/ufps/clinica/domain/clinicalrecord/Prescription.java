package co.ufps.clinica.domain.clinicalrecord;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;
import java.util.List;
public record Prescription(PrescriptionId id, EncounterId encounterId, PatientId patientId, DoctorId doctorId,
                           Instant issuedAt, String instructions, List<PrescriptionLine> lines) {
    public Prescription { lines = List.copyOf(lines); }
}
