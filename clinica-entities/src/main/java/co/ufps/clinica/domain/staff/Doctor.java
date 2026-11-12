package co.ufps.clinica.domain.staff;
import co.ufps.clinica.domain.catalog.SpecialtyId;
import co.ufps.clinica.domain.identity.UserId;
public record Doctor(DoctorId id, UserId userId, String licenseNumber, String firstName, String lastName, SpecialtyId specialtyId, StaffStatus status) {
    public Doctor {
        if (id == null || userId == null || licenseNumber == null || licenseNumber.isBlank()
                || firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()
                || specialtyId == null || status == null)
            throw new IllegalArgumentException("El perfil médico debe tener cuenta, licencia, nombre, apellidos, especialidad y estado");
    }
}
