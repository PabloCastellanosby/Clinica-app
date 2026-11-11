package co.ufps.clinica.adapters.clinicalrecord;

public record AllergyResponse(String id, String patientId, String substance, String reaction, String severity) {}