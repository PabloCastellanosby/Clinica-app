package co.ufps.clinica.application.clinic;

import java.time.ZoneId;

public record RegisterClinicCommand(String name, String taxId, String email, String phone) {}
