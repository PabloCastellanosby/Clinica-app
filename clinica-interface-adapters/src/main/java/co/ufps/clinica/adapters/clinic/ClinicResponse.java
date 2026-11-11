package co.ufps.clinica.adapters.clinic;

public record ClinicResponse(String id, String name, String taxId, String email, String phone, boolean active) {}