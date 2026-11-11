package co.ufps.clinica.application.clinicalrecord;

import co.ufps.clinica.domain.identity.UserId;
import co.ufps.clinica.domain.patient.PatientId;

public record AddMedicalHistoryCommand(PatientId patientId, String type, String description, UserId recordedBy) {}
