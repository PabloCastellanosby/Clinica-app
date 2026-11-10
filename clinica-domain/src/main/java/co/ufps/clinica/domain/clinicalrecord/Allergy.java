package co.ufps.clinica.domain.clinicalrecord;
import co.ufps.clinica.domain.patient.PatientId;
public record Allergy(AllergyId id, PatientId patientId, String substance, String reaction, String severity) {}
