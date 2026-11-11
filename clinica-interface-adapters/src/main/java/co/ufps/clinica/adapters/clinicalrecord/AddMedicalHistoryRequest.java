package co.ufps.clinica.adapters.clinicalrecord;

public record AddMedicalHistoryRequest(String patientId, String type, String description, String recordedBy) {}