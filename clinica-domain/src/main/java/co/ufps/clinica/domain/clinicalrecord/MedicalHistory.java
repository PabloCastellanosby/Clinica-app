package co.ufps.clinica.domain.clinicalrecord;
import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.patient.PatientId;
import java.time.Instant;
public record MedicalHistory(MedicalHistoryId id, PatientId patientId, String type, String description,
                             Instant recordedAt, UserId recordedBy) {}
