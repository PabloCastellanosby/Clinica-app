package co.ufps.clinica.application.staff;

import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.identity.UserId;

public record RegisterDoctorCommand(UserId userId, String licenseNumber,
                                    String firstName, String lastName, SpecialtyId specialtyId) {}
