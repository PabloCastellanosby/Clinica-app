package co.ufps.clinica.application.clinicalrecord;

import co.ufps.clinica.domain.patient.PatientId;

public record RegisterAllergyCommand(PatientId patientId, String substance, String reaction, String severity) {}
