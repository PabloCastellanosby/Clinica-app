package co.ufps.clinica.domain.clinicalrecord;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.patient.PatientId;
import java.time.Instant;
public record MedicalHistory(MedicalHistoryId id, PatientId patientId, String type, String description,
                             Instant recordedAt, UserId recordedBy) {
    public MedicalHistory {
        if (id == null || patientId == null || type == null || type.isBlank() || recordedAt == null || recordedBy == null)
            throw new IllegalArgumentException("El antecedente debe tener paciente, tipo, fecha y responsable");
    }
}
