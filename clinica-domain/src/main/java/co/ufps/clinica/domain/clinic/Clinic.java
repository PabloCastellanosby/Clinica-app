package co.ufps.clinica.domain.clinic;
public record Clinic(ClinicId id, String name, String taxId, String email, String phone, boolean active) {}
