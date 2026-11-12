package co.ufps.clinica.domain.clinicalrecord;
import co.ufps.clinica.domain.patient.PatientId;
import co.ufps.clinica.domain.staff.DoctorId;
import java.time.Instant;
import java.util.List;
public record Prescription(PrescriptionId id, EncounterId encounterId, PatientId patientId, DoctorId doctorId,
                           Instant issuedAt, String instructions, List<PrescriptionLine> lines) {
    public Prescription {
        if (id == null || encounterId == null || patientId == null || doctorId == null || issuedAt == null
                || lines == null || lines.isEmpty() || lines.stream().anyMatch(java.util.Objects::isNull))
            throw new IllegalArgumentException("La receta debe tener encuentro, paciente, médico, fecha y al menos una línea válida");
        lines = List.copyOf(lines);
    }
}
