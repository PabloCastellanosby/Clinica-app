package co.ufps.clinica.domain.staff;
import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.identity.UserId;
public record Doctor(DoctorId id, UserId userId, String licenseNumber, String firstName, String lastName, SpecialtyId specialtyId, StaffStatus status) {}
