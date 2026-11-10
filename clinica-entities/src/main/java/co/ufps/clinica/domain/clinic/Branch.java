package co.ufps.clinica.domain.clinic;
import java.time.ZoneId;
public record Branch(BranchId id, ClinicId clinicId, String name, String address, String phone, ZoneId timeZone, boolean active) {}
