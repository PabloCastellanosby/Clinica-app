package co.ufps.clinica.application.clinic;

import co.ufps.clinica.domain.clinic.ClinicId;
import java.time.ZoneId;

public record RegisterBranchCommand(ClinicId clinicId, String name, String address, String phone, ZoneId timeZone) {}
