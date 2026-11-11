package co.ufps.clinica.adapters.staff;

public record RegisterDoctorRequest(String userId, String licenseNumber, String firstName,
                                    String lastName, String specialtyId) {}