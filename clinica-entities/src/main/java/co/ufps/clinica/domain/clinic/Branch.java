package co.ufps.clinica.domain.clinic;
import java.time.ZoneId;
public record Branch(BranchId id, ClinicId clinicId, String name, String address, String phone, ZoneId timeZone, boolean active) {
    public Branch {
        if (id == null || clinicId == null || name == null || name.isBlank() || timeZone == null)
            throw new IllegalArgumentException("La sede debe tener clínica, nombre y zona horaria");
    }
}
