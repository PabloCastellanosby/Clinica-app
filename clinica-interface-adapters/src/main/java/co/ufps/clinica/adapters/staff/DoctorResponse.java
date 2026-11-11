package co.ufps.clinica.adapters.staff;

public record DoctorResponse(String id, String userId, String licenseNumber, String firstName,
                             String lastName, String specialtyId, String status) {}