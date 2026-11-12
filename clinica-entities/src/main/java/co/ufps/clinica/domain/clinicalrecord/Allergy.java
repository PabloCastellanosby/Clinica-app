package co.ufps.clinica.domain.clinicalrecord;
import co.ufps.clinica.domain.patient.PatientId;
public record Allergy(AllergyId id, PatientId patientId, String substance, String reaction, String severity) {
    public Allergy {
        if (id == null || patientId == null || substance == null || substance.isBlank())
            throw new IllegalArgumentException("La alergia debe indicar paciente y sustancia");
    }
}
