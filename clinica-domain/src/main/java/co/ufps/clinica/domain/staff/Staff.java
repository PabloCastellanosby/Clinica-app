package co.ufps.clinica.domain.staff;
import co.ufps.clinica.domain.identity.UserId;
public record Staff(StaffId id, UserId userId, String firstName, String lastName, String position, StaffStatus status) {}
