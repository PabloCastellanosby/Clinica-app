package co.ufps.clinica.adapters.clinicalrecord;

public record RegisterAllergyRequest(String patientId, String substance, String reaction, String severity) {}